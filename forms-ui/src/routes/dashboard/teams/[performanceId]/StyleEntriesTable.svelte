<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import type { TeamForm, JudgeType } from "$lib/utils/form-results";
    import StyleEntryRow from "./StyleEntryRow.svelte";

    const { entries = $bindable() } = $props<{ 
        entries: TeamForm['styleEntries'];
    }>();

    function getAllJudgeColumns(entries: TeamForm['styleEntries']): number[] {
        const maxJudge = getMaxJudgeCount(entries);
        return Array.from({ length: maxJudge }, (_, i) => i + 1);
    }

    function getMaxJudgeCount(entries: TeamForm['styleEntries']): number {
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
    {@const allJudgeColumns = getAllJudgeColumns(entries)}
    <div class="flex flex-col gap-2">
        <h2 class="text-xl font-semibold">Wpisy stylu</h2>
        <div class="rounded-md border">
            <Table.Root>
                <Table.Header>
                    <Table.Row>
                        <Table.Head>Nazwa</Table.Head>
                        <Table.Head>Nazwa stylu</Table.Head>
                        {#each allJudgeColumns as judge}
                            <Table.Head>STYL {judge}</Table.Head>
                        {/each}
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each entries as styleEntry, i (styleEntry.entry.id)}
                        <StyleEntryRow
                            bind:styleEntry={entries[i]}
                            allJudgeColumns={allJudgeColumns}
                        />
                    {/each}
                </Table.Body>
            </Table.Root>
        </div>
    </div>
{/if}

