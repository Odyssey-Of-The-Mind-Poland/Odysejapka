<script lang="ts">
    import {Button} from "$lib/components/ui/button";
    import FormCard from "./FormCard.svelte";
    import PenaltyFormEntry from "./PenaltyFormEntry.svelte";
    import type {FormEntryType, ProblemForm} from "./types";

    interface Props {
        title: string;
        entries: FormEntryType[];
        form: ProblemForm;
        onAddEntry: (category: 'penaltyEntries', type: 'PENALTY', penaltyType?: 'RANGE' | 'DISCRETE' | 'SINGLE' | 'ZERO_BALSA') => void;
        onRemoveEntry: (category: 'penaltyEntries', index: number) => void;
    }

    let {title, entries, form = $bindable(), onAddEntry, onRemoveEntry}: Props = $props();
</script>

<FormCard {title} {entries} bind:form category="penaltyEntries">
    {#snippet children({ item, index, items })}
        <PenaltyFormEntry
                bind:entry={items[index]}
                onRemove={() => onRemoveEntry('penaltyEntries', index)}
        />
    {/snippet}
    {#snippet addButtons()}
        <Button variant="outline" onclick={() => onAddEntry('penaltyEntries', 'PENALTY')}>
            Dodaj Karne
        </Button>
        <Button variant="outline" onclick={() => onAddEntry('penaltyEntries', 'PENALTY', 'ZERO_BALSA')}>
            Dodaj Zero punktów za balse
        </Button>
    {/snippet}
</FormCard>

