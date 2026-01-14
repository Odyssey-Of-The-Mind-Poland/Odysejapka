<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import * as Input from "$lib/components/ui/input/index.js";
    import type { TeamForm, JudgeType } from "$lib/utils/form-results";

    const { entries = $bindable() } = $props<{ 
        entries: TeamForm['dtEntries'];
    }>();

    function getJudgeKeys(results: Record<JudgeType, Record<number, number | string | null>>): number[] {
        const allKeys = new Set<number>();
        Object.values(results).forEach(judgeMap => {
            Object.keys(judgeMap).forEach(key => allKeys.add(Number(key)));
        });
        return Array.from(allKeys).sort((a, b) => a - b);
    }
</script>

{#if entries.length > 0}
    <div class="flex flex-col gap-2">
        <h2 class="text-xl font-semibold">Wpisy DT</h2>
        <div class="rounded-md border">
            <Table.Root>
                <Table.Header>
                    <Table.Row>
                        <Table.Head>Wpis</Table.Head>
                        <Table.Head>Typ</Table.Head>
                        <Table.Head>DT_A</Table.Head>
                        <Table.Head>DT_B</Table.Head>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each entries as dtEntry (dtEntry.entry.id)}
                        {@const judgeKeys = getJudgeKeys(dtEntry.results)}
                        <Table.Row>
                            <Table.Cell class="font-medium">{dtEntry.entry.name}</Table.Cell>
                            <Table.Cell>{dtEntry.entry.type}</Table.Cell>
                            <Table.Cell>
                                <div class="flex flex-col gap-2">
                                    {#each judgeKeys as judgeKey}
                                        <div class="flex items-center gap-2">
                                            <label for="judge-dt-a-{dtEntry.entry.id}-{judgeKey}" class="text-sm font-medium w-20">Sędzia {judgeKey}:</label>
                                            <Input.Input
                                                id="judge-dt-a-{dtEntry.entry.id}-{judgeKey}"
                                                type="number"
                                                bind:value={dtEntry.results.DT_A[judgeKey]}
                                                class="w-24"
                                            />
                                        </div>
                                    {/each}
                                </div>
                            </Table.Cell>
                            <Table.Cell>
                                <div class="flex flex-col gap-2">
                                    {#each judgeKeys as judgeKey}
                                        <div class="flex items-center gap-2">
                                            <label for="judge-dt-b-{dtEntry.entry.id}-{judgeKey}" class="text-sm font-medium w-20">Sędzia {judgeKey}:</label>
                                            <Input.Input
                                                id="judge-dt-b-{dtEntry.entry.id}-{judgeKey}"
                                                type="number"
                                                bind:value={dtEntry.results.DT_B[judgeKey]}
                                                class="w-24"
                                            />
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

