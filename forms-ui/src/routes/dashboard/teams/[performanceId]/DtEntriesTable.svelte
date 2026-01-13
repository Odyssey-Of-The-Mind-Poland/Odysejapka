<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import * as Input from "$lib/components/ui/input/index.js";
    import type { TeamForm } from "./types";

    const { entries = $bindable() } = $props<{ 
        entries: TeamForm['dtEntries'];
    }>();

    function getJudgeKeys(judgesA: Record<number, number | null>, judgesB: Record<number, number | null>): number[] {
        const allKeys = new Set([...Object.keys(judgesA).map(Number), ...Object.keys(judgesB).map(Number)]);
        return Array.from(allKeys).sort((a, b) => a - b);
    }
</script>

{#if entries.length > 0}
    <div class="flex flex-col gap-2">
        <h2 class="text-xl font-semibold">DT Entries</h2>
        <div class="rounded-md border">
            <Table.Root>
                <Table.Header>
                    <Table.Row>
                        <Table.Head>Entry</Table.Head>
                        <Table.Head>Type</Table.Head>
                        <Table.Head>Judges A</Table.Head>
                        <Table.Head>Judges B</Table.Head>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each entries as dtEntry (dtEntry.entry.id)}
                        {@const judgeKeys = getJudgeKeys(dtEntry.judgesA, dtEntry.judgesB)}
                        <Table.Row>
                            <Table.Cell class="font-medium">{dtEntry.entry.name}</Table.Cell>
                            <Table.Cell>{dtEntry.entry.type}</Table.Cell>
                            <Table.Cell>
                                <div class="flex flex-col gap-2">
                                    {#each judgeKeys as judgeKey}
                                        <div class="flex items-center gap-2">
                                            <label for="judge-a-{dtEntry.entry.id}-{judgeKey}" class="text-sm font-medium w-20">Judge {judgeKey}:</label>
                                            <Input.Input
                                                id="judge-a-{dtEntry.entry.id}-{judgeKey}"
                                                type="number"
                                                bind:value={dtEntry.judgesA[judgeKey]}
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
                                            <label for="judge-b-{dtEntry.entry.id}-{judgeKey}" class="text-sm font-medium w-20">Judge {judgeKey}:</label>
                                            <Input.Input
                                                id="judge-b-{dtEntry.entry.id}-{judgeKey}"
                                                type="number"
                                                bind:value={dtEntry.judgesB[judgeKey]}
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

