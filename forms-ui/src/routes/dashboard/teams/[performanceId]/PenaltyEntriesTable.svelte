<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import type { TeamForm } from "$lib/utils/form-results";
    import PenaltyEntryRow from "./PenaltyEntryRow.svelte";

    const { entries = $bindable() } = $props<{ 
        entries: TeamForm['penaltyEntries'];
    }>();
</script>

{#if entries.length > 0}
    <div class="flex flex-col gap-2">
        <h2 class="text-xl font-semibold">Wpisy kar</h2>
        <div class="rounded-md border">
            <Table.Root>
                <Table.Header>
                    <Table.Row>
                        <Table.Head>Wpis</Table.Head>
                        <Table.Head>Typ kary</Table.Head>
                        <Table.Head>Wartość</Table.Head>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each entries as penaltyEntry, i (penaltyEntry.entry.id)}
                        <PenaltyEntryRow
                            bind:penaltyEntry={entries[i]}
                        />
                    {/each}
                </Table.Body>
            </Table.Root>
        </div>
    </div>
{/if}

