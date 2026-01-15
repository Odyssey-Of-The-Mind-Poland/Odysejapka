<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import * as Input from "$lib/components/ui/input/index.js";
    import type {TeamForm, JudgeType} from "$lib/utils/form-results";
    import ObjectiveJudgeInput from "./ObjectiveJudgeInput.svelte";

    const {entries = $bindable()} = $props<{
        entries: TeamForm['dtEntries'];
    }>();

    function getJudgeKeys(results: Record<JudgeType, Record<number, number | string | null>>): number[] {
        const allKeys = new Set<number>();
        Object.values(results).forEach(judgeMap => {
            Object.keys(judgeMap).forEach(key => allKeys.add(Number(key)));
        });
        return Array.from(allKeys).sort((a, b) => a - b);
    }

    function getAllJudgeColumns(entries: TeamForm['dtEntries']): Array<{ type: 'DT_A' | 'DT_B', judge: number }> {
        const columnSet = new Set<string>();

        entries.forEach(entry => {
            const judgeKeys = getJudgeKeys(entry.results);
            judgeKeys.forEach(judgeKey => {
                if (entry.results.DT_A && entry.results.DT_A[judgeKey] !== undefined) {
                    columnSet.add(`DT_A-${judgeKey}`);
                }
                if (entry.results.DT_B && entry.results.DT_B[judgeKey] !== undefined) {
                    columnSet.add(`DT_B-${judgeKey}`);
                }
            });
        });

        const columns: Array<{ type: 'DT_A' | 'DT_B', judge: number }> = [];
        Array.from(columnSet).sort().forEach(key => {
            const [type, judge] = key.split('-');
            columns.push({type: type as 'DT_A' | 'DT_B', judge: Number(judge)});
        });

        return columns;
    }

    function getColumnLabel(type: 'DT_A' | 'DT_B', judge: number): string {
        const prefix = type === 'DT_A' ? 'A' : 'B';
        return `${prefix}${judge}`;
    }

    function getMaxJudgeCount(entries: TeamForm['dtEntries']): number {
        let maxJudge = 0;
        entries.forEach(entry => {
            Object.values(entry.results).forEach(judgeMap => {
                Object.keys(judgeMap).forEach(key => {
                    const judgeNum = Number(key);
                    if (judgeNum > maxJudge) {
                        maxJudge = judgeNum;
                    }
                });
            });
        });
        return maxJudge || 1;
    }

    function isColumnEnabled(
        column: { type: 'DT_A' | 'DT_B', judge: number },
        entry: TeamForm['dtEntries'][0],
        maxJudgeCount: number
    ): boolean {
        if (column.judge > maxJudgeCount) {
            return false;
        }

        const judgesConfig = entry.entry.scoring?.judges;
        if (!judgesConfig) {
            return true;
        }

        if (judgesConfig === 'A') {
            return column.type === 'DT_A';
        }
        if (judgesConfig === 'B') {
            return column.type === 'DT_B';
        }
        if (judgesConfig === 'A_PLUS_B') {
            return true;
        }

        return true;
    }
</script>

{#if entries.length > 0}
    {@const allColumns = getAllJudgeColumns(entries)}
    {@const maxJudgeCount = getMaxJudgeCount(entries)}
    <div class="flex flex-col gap-2">
        <h2 class="text-xl font-semibold">Wpisy DT</h2>
        <div class="rounded-md border">
            <Table.Root>
                <Table.Header>
                    <Table.Row>
                        <Table.Head>Nazwa</Table.Head>
                        {#each allColumns as column}
                            <Table.Head>{getColumnLabel(column.type, column.judge)}</Table.Head>
                        {/each}
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each entries as dtEntry (dtEntry.entry.id)}
                        {@const objectiveBucket = dtEntry.entry.scoring?.objectiveBucket}
                        {@const isObjective = dtEntry.entry.scoring?.scoringType === 'OBJECTIVE' && objectiveBucket}
                        <Table.Row>
                            <Table.Cell>
                                <div class="flex flex-col">
                                    <div class="font-extralight text-sm">
                                        {dtEntry.entry.sortIndex}
                                    </div>
                                    <div class="font-medium">
                                        {dtEntry.entry.name}
                                    </div>
                                </div>
                            </Table.Cell>
                            {#each allColumns as column}
                                {@const isEnabled = isColumnEnabled(column, dtEntry, maxJudgeCount)}
                                <Table.Cell>
                                    {#if isObjective && objectiveBucket}
                                        <ObjectiveJudgeInput
                                                objectiveBucketName={objectiveBucket}
                                                bind:value={dtEntry.results[column.type][column.judge]}
                                                disabled={!isEnabled}
                                        />
                                    {:else}
                                        <Input.Input
                                                id="judge-{column.type}-{dtEntry.entry.id}-{column.judge}"
                                                type="number"
                                                bind:value={dtEntry.results[column.type][column.judge]}
                                                class="w-24"
                                                disabled={!isEnabled}
                                        />
                                    {/if}
                                </Table.Cell>
                            {/each}
                        </Table.Row>
                    {/each}
                </Table.Body>
            </Table.Root>
        </div>
    </div>
{/if}

