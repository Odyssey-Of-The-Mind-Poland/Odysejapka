<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import type { TeamForm } from "./types";

    const { entries } = $props<{ entries: TeamForm['styleEntries'] }>();

    function formatJudgeValue(value: number | null | undefined): string {
        return value != null ? String(value) : '-';
    }

    function getJudgeKeys(styleJudge: Record<number, number | null>): number[] {
        return Object.keys(styleJudge)
            .map(Number)
            .sort((a, b) => a - b);
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
                        <Table.Head>Style Judge</Table.Head>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each entries as styleEntry (styleEntry.entry.id)}
                        {@const judgeKeys = getJudgeKeys(styleEntry.styleJudge)}
                        <Table.Row>
                            <Table.Cell class="font-medium">{styleEntry.entry.name}</Table.Cell>
                            <Table.Cell>{styleEntry.entry.styleType}</Table.Cell>
                            <Table.Cell>
                                <div class="flex flex-col gap-1">
                                    {#each judgeKeys as judgeKey}
                                        <div class="text-sm">
                                            <span class="font-medium">Judge {judgeKey}:</span> {formatJudgeValue(styleEntry.styleJudge[judgeKey])}
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

