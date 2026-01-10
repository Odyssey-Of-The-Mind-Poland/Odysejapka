<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import * as Collapsible from '$lib/components/ui/collapsible/index.js';
    import {Button} from '$lib/components/ui/button';
    import Trash2Icon from '@lucide/svelte/icons/trash-2';
    import ChevronDownIcon from "@lucide/svelte/icons/chevron-down";
    import GripVerticalIcon from "@lucide/svelte/icons/grip-vertical";
    import {dndzone} from 'svelte-dnd-action';
    import type {FormEntryType} from "./types";
    import {defaultEntry} from "./types";
    import {recalculateSortIndexes, formatSortIndex} from "./sortIndexUtils";
    import EntryNameInput from "./EntryNameInput.svelte";
    import EntryTypeSelect from "./EntryTypeSelect.svelte";
    import LongTermFormEntry from "./LongTermFormEntry.svelte";
    import SectionFormEntrySelf from "./SectionFormEntry.svelte";

    interface Props {
        entry: FormEntryType;
        onRemove?: () => void;
        parentIndex?: string;
    }

    let {entry = $bindable(), onRemove, parentIndex}: Props = $props();

    let displayIndex = $derived(formatSortIndex(entry, parentIndex));
    let isOpen = $state(true);
    let nestedItems = $state(entry.entries ?? []);

    $effect(() => {
        nestedItems = entry.entries ?? [];
    });

    function addNestedEntry(type: 'SCORING' | 'SECTION') {
        const newEntry = defaultEntry(type);
        entry.entries = recalculateSortIndexes([...(entry.entries || []), newEntry]);
    }

    function removeNestedEntry(index: number) {
        entry.entries = recalculateSortIndexes(entry.entries.filter((_, i) => i !== index));
    }

    function handleNestedSort(e: CustomEvent) {
        nestedItems = e.detail.items;
        entry.entries = recalculateSortIndexes(nestedItems);
    }
</script>

<Collapsible.Root bind:open={isOpen}>
    <Card.Root>
        <Collapsible.Trigger class="w-full flex items-center justify-between p-2 cursor-pointer">
            <div class="flex items-center gap-2">
                <GripVerticalIcon class="h-4 w-4 text-gray-400 cursor-grab active:cursor-grabbing" />
                <span class="font-semibold text-gray-700 min-w-[2rem]">{displayIndex}.</span>
            </div>
            <div class="flex items-center gap-2">
                <ChevronDownIcon class="h-4 w-4 transition-transform duration-200 {isOpen ? 'rotate-180' : ''}"/>
                {#if onRemove}
                    <Button
                            variant="ghost"
                            size="icon"
                            onclick={(e) => {
                                e.stopPropagation();
                                onRemove?.();
                            }}
                            class="shrink-0"
                            title="Usuń wpis"
                    >
                        <Trash2Icon class="h-4 w-4 text-destructive"/>
                    </Button>
                {/if}
            </div>
        </Collapsible.Trigger>
        <Collapsible.Content>
            <div class="flex flex-col gap-4 p-2">
                <div class="flex items-center gap-4">
                    <EntryTypeSelect bind:value={entry.type}/>
                    <EntryNameInput bind:value={entry.name} id={entry.id}/>
                </div>
                <div
                    use:dndzone={{ items: nestedItems }}
                    onconsider={handleNestedSort}
                    onfinalize={handleNestedSort}
                    class="flex flex-col gap-2 pl-4 border-l-2 border-gray-300"
                >
                    {#each nestedItems as nestedEntry, index (nestedEntry.id ?? nestedEntry)}
                        {#if nestedEntry.type === 'SCORING'}
                            <LongTermFormEntry
                                    bind:entry={nestedItems[index]}
                                    onRemove={() => removeNestedEntry(index)}
                                    parentIndex={displayIndex}
                            />
                        {:else if nestedEntry.type === 'SECTION' || nestedEntry.type === 'SCORING_GROUP'}
                            <SectionFormEntrySelf
                                    bind:entry={nestedItems[index]}
                                    onRemove={() => removeNestedEntry(index)}
                                    parentIndex={displayIndex}
                            />
                        {/if}
                    {/each}
                    <div class="flex gap-2 flex-wrap">
                        <Button variant="outline" size="sm" onclick={() => addNestedEntry('SCORING')}>
                            Dodaj Kategorie
                        </Button>
                        <Button variant="outline" size="sm" onclick={() => addNestedEntry('SECTION')}>
                            Dodaj Sekcje
                        </Button>
                    </div>
                </div>
            </div>
        </Collapsible.Content>
    </Card.Root>
</Collapsible.Root>

