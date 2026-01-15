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
            toast.success('Wyniki drużyny zostały zapisane pomyślnie');
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
            {name: 'Drużyny', href: '/dashboard/teams'},
            {name: `Drużyna ${performanceId}`, href: `/dashboard/teams/${performanceId}`}
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex justify-between items-center">
        <div class="flex flex-col gap-2">
            {#if formData}
                <h1 class="text-2xl font-bold">Formularz drużyny - Występ {formData.teamName}</h1>
                <div class="flex gap-4 text-sm text-gray-600">
                    <div><span class="font-medium">Miasto:</span> {formData.cityName}</div>
                    <div><span class="font-medium">Problem:</span> {formData.problem}</div>
                    <div><span class="font-medium">Wiek:</span> {formData.age}</div>
                </div>
            {/if}
        </div>
        <Button 
            onclick={handleSave}
            disabled={saveMutation.isPending || !formData}
        >
            {saveMutation.isPending ? 'Zapisywanie...' : 'Zapisz wyniki'}
        </Button>
    </div>

    {#if teamFormQuery.isPending}
        <div class="flex justify-center items-center py-8">
            <Spinner size="sm"/>
        </div>
    {:else if teamFormQuery.error}
        <div class="text-red-500">
            Błąd ładowania formularza drużyny: {String(teamFormQuery.error)}
        </div>
    {:else if formData}
        <DtEntriesTable bind:entries={formData.dtEntries} isFo={formData.isFo} />
        <StyleEntriesTable bind:entries={formData.styleEntries} />
        <PenaltyEntriesTable bind:entries={formData.penaltyEntries} />

        {#if formData.dtEntries.length === 0 && formData.styleEntries.length === 0 && formData.penaltyEntries.length === 0}
            <div class="text-gray-500">Nie znaleziono wpisów dla tej drużyny.</div>
        {/if}
    {/if}
</div>

