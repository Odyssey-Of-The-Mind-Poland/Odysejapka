<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {Button} from "$lib/components/ui/button";
    import LongTermFormEntry from "./LongTermFormEntry.svelte";
    import type {FormEntryType, ProblemForm} from "./types";

    interface Props {
        title: string;
        entries: FormEntryType[];
        form: ProblemForm;
        onAddEntry: (category: 'dtEntries', type: 'SCORING') => void;
        onRemoveEntry: (category: 'dtEntries', index: number) => void;
    }

    let {title, entries, form = $bindable(), onAddEntry, onRemoveEntry}: Props = $props();
</script>

<Card.Root>
    <Card.Header>
        <Card.Title>{title}</Card.Title>
    </Card.Header>
    <Card.Content class="flex flex-col gap-4 p-2">
        {#each entries ?? [] as entry, index (entry.id ?? index)}
            <LongTermFormEntry
                    bind:entry={form.dtEntries[index]}
                    onRemove={() => onRemoveEntry('dtEntries', index)}
            />
        {/each}
        <div class="flex gap-2 flex-wrap">
            <Button variant="outline" onclick={() => onAddEntry('dtEntries', 'SCORING')}>
                Dodaj Kategorie
            </Button>
        </div>
    </Card.Content>
</Card.Root>

