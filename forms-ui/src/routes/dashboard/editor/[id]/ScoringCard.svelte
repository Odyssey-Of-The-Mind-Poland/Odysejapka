<script lang="ts">
    import {Button} from "$lib/components/ui/button";
    import FormCard from "./FormCard.svelte";
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
</script>

<FormCard {title} {entries} bind:form category="dtEntries">
    {#snippet children({ item, index, items })}
        {#if item.type === 'SCORING'}
            <LongTermFormEntry
                    bind:entry={items[index]}
                    onRemove={() => onRemoveEntry('dtEntries', index)}
            />
        {:else if item.type === 'SECTION' || item.type === 'SCORING_GROUP'}
            <SectionFormEntry
                    bind:entry={items[index]}
                    onRemove={() => onRemoveEntry('dtEntries', index)}
            />
        {/if}
    {/snippet}
    {#snippet addButtons()}
        <Button variant="outline" onclick={() => onAddEntry('dtEntries', 'SCORING')}>
            Dodaj Kategorie
        </Button>
        <Button variant="outline" onclick={() => onAddEntry('dtEntries', 'SECTION')}>
            Dodaj Sekcje
        </Button>
    {/snippet}
</FormCard>

