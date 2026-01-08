<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {Button} from "$lib/components/ui/button";
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

<Card.Root>
    <Card.Header>
        <Card.Title>{title}</Card.Title>
    </Card.Header>
    <Card.Content class="flex flex-col gap-4 p-2">
        {#each entries ?? [] as entry, index (entry.id ?? index)}
            <StyleFormEntry
                    bind:entry={form.styleEntries[index]}
                    onRemove={() => onRemoveEntry('styleEntries', index)}
            />
        {/each}
        <div class="flex gap-2 flex-wrap">
            <Button variant="outline" onclick={() => onAddEntry('styleEntries', 'STYLE')}>
                Dodaj Styl
            </Button>
        </div>
    </Card.Content>
</Card.Root>

