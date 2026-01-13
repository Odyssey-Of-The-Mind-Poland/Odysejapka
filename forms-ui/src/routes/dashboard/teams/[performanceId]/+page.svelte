<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {page} from "$app/state";
    import * as Table from "$lib/components/ui/table/index.js";
    import {onMount} from "svelte";

    type LongTermFormEntry = {
        id: number | null;
        name: string;
        type: 'SCORING' | 'SECTION' | 'SCORING_GROUP';
        scoring?: {
            scoringType: 'SUBJECTIVE' | 'OBJECTIVE';
            noElementEnabled: boolean;
            subjectiveRange?: string;
            objectiveBucket?: string;
        } | null;
        entries: LongTermFormEntry[];
        sortIndex: number;
    };

    type StyleFormEntry = {
        id: number | null;
        name: string;
        type: 'STYLE';
        styleType: 'PREDEFINED' | 'FREE_TEAM_CHOICE';
        entries: StyleFormEntry[];
        sortIndex: number;
    };

    type PenaltyFormEntry = {
        id: number | null;
        name: string;
        type: 'PENALTY';
        penaltyType?: 'RANGE' | 'DISCRETE' | 'SINGLE' | 'ZERO_BALSA' | null;
        penaltyRange?: { min: number; max: number } | null;
        penaltyDiscrete?: { values: number[] } | null;
        penaltySingle?: { value: number } | null;
        entries: PenaltyFormEntry[];
        sortIndex: number;
    };

    type TeamForm = {
        performanceId: number;
        dtEntries: Array<{
            entry: LongTermFormEntry;
            judgeResults: Record<number, number>;
        }>;
        styleEntries: Array<{
            entry: StyleFormEntry;
            judgeResults: Record<number, number>;
        }>;
        penaltyEntries: Array<{
            entry: PenaltyFormEntry;
            judgeResults: Record<number, number>;
        }>;
    };

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

    function renderJudgeResults(judgeResults: Record<number, number>): string {
        const entries = Object.entries(judgeResults)
            .map(([judge, result]) => `Judge ${judge}: ${result}`)
            .join(', ');
        return entries || 'No results';
    }

    function renderEntryName(entry: LongTermFormEntry | StyleFormEntry | PenaltyFormEntry, depth: number = 0): string {
        const indent = '  '.repeat(depth);
        return `${indent}${entry.name}`;
    }
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
        
        {#if form.dtEntries.length > 0}
            <div class="flex flex-col gap-2">
                <h2 class="text-xl font-semibold">DT Entries</h2>
                <div class="rounded-md border">
                    <Table.Root>
                        <Table.Header>
                            <Table.Row>
                                <Table.Head>Entry</Table.Head>
                                <Table.Head>Type</Table.Head>
                                <Table.Head>Judge Results</Table.Head>
                            </Table.Row>
                        </Table.Header>
                        <Table.Body>
                            {#each form.dtEntries as dtEntry (dtEntry.entry.id)}
                                <Table.Row>
                                    <Table.Cell class="font-medium">{dtEntry.entry.name}</Table.Cell>
                                    <Table.Cell>{dtEntry.entry.type}</Table.Cell>
                                    <Table.Cell>{renderJudgeResults(dtEntry.judgeResults)}</Table.Cell>
                                </Table.Row>
                            {/each}
                        </Table.Body>
                    </Table.Root>
                </div>
            </div>
        {/if}

        {#if form.styleEntries.length > 0}
            <div class="flex flex-col gap-2">
                <h2 class="text-xl font-semibold">Style Entries</h2>
                <div class="rounded-md border">
                    <Table.Root>
                        <Table.Header>
                            <Table.Row>
                                <Table.Head>Entry</Table.Head>
                                <Table.Head>Type</Table.Head>
                                <Table.Head>Judge Results</Table.Head>
                            </Table.Row>
                        </Table.Header>
                        <Table.Body>
                            {#each form.styleEntries as styleEntry (styleEntry.entry.id)}
                                <Table.Row>
                                    <Table.Cell class="font-medium">{styleEntry.entry.name}</Table.Cell>
                                    <Table.Cell>{styleEntry.entry.styleType}</Table.Cell>
                                    <Table.Cell>{renderJudgeResults(styleEntry.judgeResults)}</Table.Cell>
                                </Table.Row>
                            {/each}
                        </Table.Body>
                    </Table.Root>
                </div>
            </div>
        {/if}

        {#if form.penaltyEntries.length > 0}
            <div class="flex flex-col gap-2">
                <h2 class="text-xl font-semibold">Penalty Entries</h2>
                <div class="rounded-md border">
                    <Table.Root>
                        <Table.Header>
                            <Table.Row>
                                <Table.Head>Entry</Table.Head>
                                <Table.Head>Penalty Type</Table.Head>
                                <Table.Head>Judge Results</Table.Head>
                            </Table.Row>
                        </Table.Header>
                        <Table.Body>
                            {#each form.penaltyEntries as penaltyEntry (penaltyEntry.entry.id)}
                                <Table.Row>
                                    <Table.Cell class="font-medium">{penaltyEntry.entry.name}</Table.Cell>
                                    <Table.Cell>{penaltyEntry.entry.penaltyType || 'N/A'}</Table.Cell>
                                    <Table.Cell>{renderJudgeResults(penaltyEntry.judgeResults)}</Table.Cell>
                                </Table.Row>
                            {/each}
                        </Table.Body>
                    </Table.Root>
                </div>
            </div>
        {/if}

        {#if form.dtEntries.length === 0 && form.styleEntries.length === 0 && form.penaltyEntries.length === 0}
            <div class="text-gray-500">No entries found for this team.</div>
        {/if}
    {/if}
</div>

