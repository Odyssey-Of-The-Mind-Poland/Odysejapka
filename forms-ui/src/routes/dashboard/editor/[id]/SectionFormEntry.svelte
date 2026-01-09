<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {Button} from '$lib/components/ui/button';
    import Trash2Icon from '@lucide/svelte/icons/trash-2';
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

    function addNestedEntry(type: 'SCORING' | 'SECTION') {
        const newEntry = defaultEntry(type);
        entry.entries = recalculateSortIndexes([...(entry.entries || []), newEntry]);
    }

    function removeNestedEntry(index: number) {
        entry.entries = recalculateSortIndexes(entry.entries.filter((_, i) => i !== index));
    }
</script>

<Card.Root>
    <div class="flex flex-col gap-4 p-2">
        <div class="flex items-center justify-between">
            <span class="font-semibold text-gray-700 min-w-[2rem]">{displayIndex}.</span>
            {#if onRemove}
                <Button
                        variant="ghost"
                        size="icon"
                        onclick={onRemove}
                        class="shrink-0"
                        title="Usuń wpis"
                >
                    <Trash2Icon class="h-4 w-4 text-destructive"/>
                </Button>
            {/if}
        </div>
        <div class="flex items-center gap-4">
            <EntryTypeSelect bind:value={entry.type}/>
            <EntryNameInput bind:value={entry.name} id={entry.id}/>
        </div>
        <div class="flex flex-col gap-2 pl-4 border-l-2 border-gray-300">
            {#each entry.entries ?? [] as nestedEntry, index (nestedEntry.id ?? index)}
                {#if nestedEntry.type === 'SCORING'}
                    <LongTermFormEntry
                            bind:entry={entry.entries[index]}
                            onRemove={() => removeNestedEntry(index)}
                            parentIndex={displayIndex}
                    />
                {:else if nestedEntry.type === 'SECTION' || nestedEntry.type === 'SCORING_GROUP'}
                    <SectionFormEntrySelf
                            bind:entry={entry.entries[index]}
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
</Card.Root>

