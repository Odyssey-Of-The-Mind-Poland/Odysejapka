<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {Button} from "$lib/components/ui/button";
    import FormEntry from "./FormEntry.svelte";
    import type {FormEntryType, ProblemForm} from "./types";

    type Category = 'dtEntries' | 'styleEntries' | 'penaltyEntries';

    interface Props {
        title: string;
        category: Category;
        entries: FormEntryType[];
        form: ProblemForm;
        onAddEntry: (category: Category, type: 'SCORING' | 'SECTION' | 'SCORING_GROUP') => void;
    }

    let {title, category, entries, form, onAddEntry}: Props = $props();
</script>

<Card.Root>
    <Card.Header>
        <Card.Title>{title}</Card.Title>
    </Card.Header>
    <Card.Content class="flex flex-col gap-4">
        {#each entries ?? [] as entry, index (entry.id ?? index)}
            <FormEntry
                    bind:entry={form[category][index]}
            />
        {/each}
        <div class="flex gap-2 flex-wrap">
            <Button variant="outline" onclick={() => onAddEntry(category, 'SCORING')}>
                Dodaj Punktację
            </Button>
            <Button variant="outline" onclick={() => onAddEntry(category, 'SECTION')}>
                Dodaj Sekcję
            </Button>
            <Button variant="outline" onclick={() => onAddEntry(category, 'SCORING_GROUP')}>
                Dodaj Grupę Punktacji
            </Button>
        </div>
    </Card.Content>
</Card.Root>

