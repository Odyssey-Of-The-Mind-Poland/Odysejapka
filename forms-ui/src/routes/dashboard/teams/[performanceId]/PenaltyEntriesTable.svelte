<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import type { TeamForm } from "./types";

    const { entries } = $props<{ entries: TeamForm['penaltyEntries'] }>();

    function formatJudgeValue(value: number | null | undefined): string {
        return value != null ? String(value) : '-';
    }

    function getJudgeKeys(penalty: Record<number, number | null>): number[] {
        return Object.keys(penalty)
            .map(Number)
            .sort((a, b) => a - b);
    }
</script>

{#if entries.length > 0}
    <div class="flex flex-col gap-2">
        <h2 class="text-xl font-semibold">Penalty Entries</h2>
        <div class="rounded-md border">
            <Table.Root>
                <Table.Header>
                    <Table.Row>
                        <Table.Head>Entry</Table.Head>
                        <Table.Head>Penalty Type</Table.Head>
                        <Table.Head>Penalty</Table.Head>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each entries as penaltyEntry (penaltyEntry.entry.id)}
                        {@const judgeKeys = getJudgeKeys(penaltyEntry.penalty)}
                        <Table.Row>
                            <Table.Cell class="font-medium">{penaltyEntry.entry.name}</Table.Cell>
                            <Table.Cell>{penaltyEntry.entry.penaltyType || 'N/A'}</Table.Cell>
                            <Table.Cell>
                                <div class="flex flex-col gap-1">
                                    {#each judgeKeys as judgeKey}
                                        <div class="text-sm">
                                            <span class="font-medium">Judge {judgeKey}:</span> {formatJudgeValue(penaltyEntry.penalty[judgeKey])}
                                        </div>
                                    {/each}
                                </div>
                            </Table.Cell>
                        </Table.Row>
                    {/each}
                </Table.Body>
            </Table.Root>
        </div>
    </div>
{/if}

