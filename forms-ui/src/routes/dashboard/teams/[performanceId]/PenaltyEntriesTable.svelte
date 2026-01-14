<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import * as Input from "$lib/components/ui/input/index.js";
    import type { TeamForm } from "$lib/utils/form-results";

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
                    {#each entries as penaltyEntry (penaltyEntry.entry.id)}
                        <Table.Row>
                            <Table.Cell class="font-medium">{penaltyEntry.entry.name}</Table.Cell>
                            <Table.Cell>{penaltyEntry.entry.penaltyType || 'N/A'}</Table.Cell>
                            <Table.Cell>
                                <Input.Input
                                    id="penalty-value-{penaltyEntry.entry.id}"
                                    type="number"
                                    bind:value={penaltyEntry.result}
                                    class="w-24"
                                />
                            </Table.Cell>
                        </Table.Row>
                    {/each}
                </Table.Body>
            </Table.Root>
        </div>
    </div>
{/if}

