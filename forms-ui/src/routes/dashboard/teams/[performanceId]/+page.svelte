<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {page} from "$app/state";
    import {onMount} from "svelte";
    import type { TeamForm } from "./types";
    import DtEntriesTable from "./DtEntriesTable.svelte";
    import StyleEntriesTable from "./StyleEntriesTable.svelte";
    import PenaltyEntriesTable from "./PenaltyEntriesTable.svelte";

    let performanceId = $derived(Number(page.params.performanceId));

    let teamFormQuery = createOdysejaQuery<TeamForm>({
        queryKey: ['teamForm', performanceId],
        path: `/api/v1/form/${performanceId}/result`,
    });

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
    </div>

    {#if teamFormQuery.isPending}
        <div class="flex justify-center items-center py-8">
            <Spinner size="sm"/>
        </div>
    {:else if teamFormQuery.error}
        <div class="text-red-500">
            Error loading team form: {String(teamFormQuery.error)}
        </div>
    {:else if teamFormQuery.data}
        {@const form = teamFormQuery.data}
        
        <DtEntriesTable entries={form.dtEntries} />
        <StyleEntriesTable entries={form.styleEntries} />
        <PenaltyEntriesTable entries={form.penaltyEntries} />

        {#if form.dtEntries.length === 0 && form.styleEntries.length === 0 && form.penaltyEntries.length === 0}
            <div class="text-gray-500">No entries found for this team.</div>
        {/if}
    {/if}
</div>

