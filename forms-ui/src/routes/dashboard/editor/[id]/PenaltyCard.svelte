<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {Button} from "$lib/components/ui/button";
    import PenaltyFormEntry from "./PenaltyFormEntry.svelte";
    import type {FormEntryType, ProblemForm} from "./types";

    interface Props {
        title: string;
        entries: FormEntryType[];
        form: ProblemForm;
        onAddEntry: (category: 'penaltyEntries', type: 'PENALTY') => void;
        onRemoveEntry: (category: 'penaltyEntries', index: number) => void;
    }

    let {title, entries, form = $bindable(), onAddEntry, onRemoveEntry}: Props = $props();
</script>

<Card.Root>
    <Card.Header>
        <Card.Title>{title}</Card.Title>
    </Card.Header>
    <Card.Content class="flex flex-col gap-4 p-2">
        {#each entries ?? [] as entry, index (entry.id ?? index)}
            <PenaltyFormEntry
                    bind:entry={form.penaltyEntries[index]}
                    onRemove={() => onRemoveEntry('penaltyEntries', index)}
            />
        {/each}
        <div class="flex gap-2 flex-wrap">
            <Button variant="outline" onclick={() => onAddEntry('penaltyEntries', 'PENALTY')}>
                Dodaj Karne
            </Button>
        </div>
    </Card.Content>
</Card.Root>

