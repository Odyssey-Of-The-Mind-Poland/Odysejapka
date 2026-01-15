<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import type {TeamForm, JudgeType} from "$lib/utils/form-results";
    import DtEntryRow from "./DtEntryRow.svelte";

    const {entries = $bindable(), isFo = false, showHeader = true} = $props<{
        entries: TeamForm['dtEntries'];
        isFo: boolean;
        showHeader?: boolean;
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
</script>

{#if entries.length > 0}
    {@const allColumns = getAllJudgeColumns(entries)}
    {@const maxJudgeCount = getMaxJudgeCount(entries)}
    <div class="flex flex-col gap-2">
        {#if showHeader}
            <h2 class="text-xl font-semibold">Punktacja Długoterminowa</h2>
        {/if}
        <div class="rounded-md border">
            <Table.Root>
                {#if showHeader}
                    <Table.Header>
                        <Table.Row>
                            <Table.Head>Nazwa</Table.Head>
                            {#each allColumns as column}
                                <Table.Head>{getColumnLabel(column.type, column.judge)}</Table.Head>
                            {/each}
                            <Table.Head>
                                Brak elementu
                            </Table.Head>
                        </Table.Row>
                    </Table.Header>
                {/if}
                <Table.Body>
                    {#each entries as dtEntry, i (dtEntry.entry.id)}
                        <DtEntryRow
                            bind:dtEntry={entries[i]}
                            allColumns={allColumns}
                            maxJudgeCount={maxJudgeCount}
                            isFo={isFo}
                        />
                    {/each}
                </Table.Body>
            </Table.Root>
        </div>
    </div>
{/if}

