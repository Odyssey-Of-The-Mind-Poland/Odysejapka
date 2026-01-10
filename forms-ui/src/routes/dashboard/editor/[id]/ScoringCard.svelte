<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import * as Collapsible from '$lib/components/ui/collapsible/index.js';
    import {Button} from "$lib/components/ui/button";
    import ChevronDownIcon from "@lucide/svelte/icons/chevron-down";
    import LongTermFormEntry from "./LongTermFormEntry.svelte";
    import SectionFormEntry from "./SectionFormEntry.svelte";
    import type {FormEntryType, ProblemForm} from "./types";

    interface Props {
        title: string;
        entries: FormEntryType[];
        form: ProblemForm;
        onAddEntry: (category: 'dtEntries', type: 'SCORING' | 'SECTION') => void;
        onRemoveEntry: (category: 'dtEntries', index: number) => void;
    }

    let {title, entries, form = $bindable(), onAddEntry, onRemoveEntry}: Props = $props();
    let isOpen = $state(true);
</script>

<Collapsible.Root bind:open={isOpen}>
    <Card.Root>
        <Card.Header>
            <Collapsible.Trigger class="w-full cursor-pointer">
                <div class="flex items-center justify-between">
                    <Card.Title>{title}</Card.Title>
                    <ChevronDownIcon class="h-4 w-4 transition-transform duration-200 {isOpen ? 'rotate-180' : ''}" />
                </div>
            </Collapsible.Trigger>
        </Card.Header>
        <Collapsible.Content>
            <Card.Content class="flex flex-col gap-4 p-2">
        {#each entries ?? [] as entry, index (entry.id ?? index)}
            {#if entry.type === 'SCORING'}
                <LongTermFormEntry
                        bind:entry={form.dtEntries[index]}
                        onRemove={() => onRemoveEntry('dtEntries', index)}
                />
            {:else if entry.type === 'SECTION' || entry.type === 'SCORING_GROUP'}
                <SectionFormEntry
                        bind:entry={form.dtEntries[index]}
                        onRemove={() => onRemoveEntry('dtEntries', index)}
                />
            {/if}
        {/each}
        <div class="flex gap-2 flex-wrap">
            <Button variant="outline" onclick={() => onAddEntry('dtEntries', 'SCORING')}>
                Dodaj Kategorie
            </Button>
            <Button variant="outline" onclick={() => onAddEntry('dtEntries', 'SECTION')}>
                Dodaj Sekcje
            </Button>
            </div>
            </Card.Content>
        </Collapsible.Content>
    </Card.Root>
</Collapsible.Root>

