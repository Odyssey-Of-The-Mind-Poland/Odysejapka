<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {Button} from "$lib/components/ui/button/index.js";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {page} from "$app/state";
    import {onMount} from "svelte";
    import {toast} from "svelte-sonner";
    import {buildResults, type TeamForm, type PerformanceResultsRequest} from "$lib/utils/form-results";
    import DtEntriesTable from "./DtEntriesTable.svelte";
    import StyleEntriesTable from "./StyleEntriesTable.svelte";
    import PenaltyEntriesTable from "./PenaltyEntriesTable.svelte";

    let performanceId = $derived(Number(page.params.performanceId));
    let performanceIdParam = $derived(page.params.performanceId);

    let teamFormQuery = $derived(createOdysejaQuery<TeamForm>({
        queryKey: ['teamForm', performanceIdParam],
        path: `/api/v1/form/${performanceIdParam}/result`,
    }));

    let formData = $state<TeamForm | null>(null);

    $effect(() => {
        if (teamFormQuery.data) {
            formData = JSON.parse(JSON.stringify(teamFormQuery.data));
        }
    });

    let saveMutation = $derived(createPutMutation<unknown, PerformanceResultsRequest>({
        path: () => `/api/v1/form/${performanceIdParam}/result`,
        queryKey: ['teamForm', performanceIdParam],
        onSuccess: () => {
            toast.success('Team results saved successfully');
        }
    }));

    function handleSave() {
        if (!formData) return;
        
        const results = buildResults(formData);
        const request: PerformanceResultsRequest = { results };
        
        saveMutation.mutate(request);
    }

    onMount(() => {
        setBreadcrumbs([
            {name: 'Teams', href: '/dashboard/teams'},
            {name: `Team ${performanceId}`, href: `/dashboard/teams/${performanceId}`}
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex justify-between items-center">
        <h1 class="text-2xl font-bold">Team Form - Performance {performanceId}</h1>
        <Button 
            onclick={handleSave}
            disabled={saveMutation.isPending || !formData}
        >
            {saveMutation.isPending ? 'Saving...' : 'Save Results'}
        </Button>
    </div>

    {#if teamFormQuery.isPending}
        <div class="flex justify-center items-center py-8">
            <Spinner size="sm"/>
        </div>
    {:else if teamFormQuery.error}
        <div class="text-red-500">
            Error loading team form: {String(teamFormQuery.error)}
        </div>
    {:else if formData}
        <DtEntriesTable bind:entries={formData.dtEntries} />
        <StyleEntriesTable bind:entries={formData.styleEntries} />
        <PenaltyEntriesTable bind:entries={formData.penaltyEntries} />

        {#if formData.dtEntries.length === 0 && formData.styleEntries.length === 0 && formData.penaltyEntries.length === 0}
            <div class="text-gray-500">No entries found for this team.</div>
        {/if}
    {/if}
</div>

