<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {Button} from "$lib/components/ui/button/index.js";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {page} from "$app/state";
    import {onMount} from "svelte";
    import {toast} from "svelte-sonner";
    import type { TeamForm, PerformanceResultsRequest, PerformanceResult } from "./types";
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

    function buildResults(): PerformanceResult[] {
        if (!formData) return [];
        
        const results: PerformanceResult[] = [];
        
        // Helper to convert value to number or null
        const toNumberOrNull = (value: number | string | null | undefined): number | null => {
            if (value === null || value === undefined || value === '') return null;
            const num = typeof value === 'string' ? Number(value) : value;
            return isNaN(num) ? null : num;
        };
        
        // Get judge count from the data structure
        const getJudgeCount = () => {
            if (formData!.dtEntries.length > 0) {
                const judgeKeys = Object.keys(formData!.dtEntries[0].judgesA).map(Number);
                return Math.max(...judgeKeys, 0);
            }
            if (formData!.styleEntries.length > 0) {
                const judgeKeys = Object.keys(formData!.styleEntries[0].styleJudge).map(Number);
                return Math.max(...judgeKeys, 0);
            }
            return 0;
        };
        
        const judgeCount = getJudgeCount();
        
        // DT entries - judgesA and judgesB
        formData.dtEntries.forEach(dtEntry => {
            Object.entries(dtEntry.judgesA).forEach(([judge, value]) => {
                const numValue = toNumberOrNull(value);
                if (numValue != null) {
                    results.push({
                        entryId: dtEntry.entry.id!,
                        judge: Number(judge),
                        result: numValue
                    });
                }
            });
            Object.entries(dtEntry.judgesB).forEach(([judge, value]) => {
                const numValue = toNumberOrNull(value);
                if (numValue != null) {
                    // For judgesB, offset by judgeCount: judgesB judge 1 maps to actual judge (judgeCount + 1)
                    results.push({
                        entryId: dtEntry.entry.id!,
                        judge: judgeCount + Number(judge),
                        result: numValue
                    });
                }
            });
        });
        
        // Style entries
        formData.styleEntries.forEach(styleEntry => {
            Object.entries(styleEntry.styleJudge).forEach(([judge, value]) => {
                const numValue = toNumberOrNull(value);
                if (numValue != null) {
                    results.push({
                        entryId: styleEntry.entry.id!,
                        judge: Number(judge),
                        result: numValue
                    });
                }
            });
        });
        
        // Penalty entries
        formData.penaltyEntries.forEach(penaltyEntry => {
            Object.entries(penaltyEntry.penalty).forEach(([judge, value]) => {
                const numValue = toNumberOrNull(value);
                if (numValue != null) {
                    results.push({
                        entryId: penaltyEntry.entry.id!,
                        judge: Number(judge),
                        result: numValue
                    });
                }
            });
        });
        
        return results;
    }

    function handleSave() {
        if (!formData) return;
        
        const results = buildResults();
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

