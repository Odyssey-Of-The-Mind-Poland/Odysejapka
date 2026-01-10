<script lang="ts">
    import {Button} from "$lib/components/ui/button";
    import FormCard from "./FormCard.svelte";
    import StyleFormEntry from "./StyleFormEntry.svelte";
    import type {FormEntryType, ProblemForm} from "./types";

    interface Props {
        title: string;
        entries: FormEntryType[];
        form: ProblemForm;
        onAddEntry: (category: 'styleEntries', type: 'STYLE') => void;
        onRemoveEntry: (category: 'styleEntries', index: number) => void;
    }

    let {title, entries, form = $bindable(), onAddEntry, onRemoveEntry}: Props = $props();
</script>

<FormCard {title} {entries} bind:form category="styleEntries">
    {#snippet children({ item, index, items })}
        <StyleFormEntry
                bind:entry={items[index]}
                onRemove={() => onRemoveEntry('styleEntries', index)}
        />
    {/snippet}
    {#snippet addButtons()}
        <Button variant="outline" onclick={() => onAddEntry('styleEntries', 'STYLE')}>
            Dodaj Styl
        </Button>
    {/snippet}
</FormCard>

