<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import * as Input from "$lib/components/ui/input/index.js";
    import type { TeamForm, JudgeType } from "$lib/utils/form-results";

    const { entries = $bindable() } = $props<{ 
        entries: TeamForm['styleEntries'];
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
        <h2 class="text-xl font-semibold">Style Entries</h2>
        <div class="rounded-md border">
            <Table.Root>
                <Table.Header>
                    <Table.Row>
                        <Table.Head>Entry</Table.Head>
                        <Table.Head>Type</Table.Head>
                        <Table.Head>STYLE</Table.Head>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each entries as styleEntry (styleEntry.entry.id)}
                        {@const judgeKeys = getJudgeKeys(styleEntry.results)}
                        <Table.Row>
                            <Table.Cell class="font-medium">{styleEntry.entry.name}</Table.Cell>
                            <Table.Cell>{styleEntry.entry.styleType}</Table.Cell>
                            <Table.Cell>
                                <div class="flex flex-col gap-2">
                                    {#each judgeKeys as judgeKey}
                                        <div class="flex items-center gap-2">
                                            <label for="style-judge-{styleEntry.entry.id}-{judgeKey}" class="text-sm font-medium w-20">Judge {judgeKey}:</label>
                                            <Input.Input
                                                id="style-judge-{styleEntry.entry.id}-{judgeKey}"
                                                type="number"
                                                bind:value={styleEntry.results.STYLE[judgeKey]}
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

