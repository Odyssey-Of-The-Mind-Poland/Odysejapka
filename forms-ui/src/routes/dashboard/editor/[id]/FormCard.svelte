<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import * as Collapsible from '$lib/components/ui/collapsible/index.js';
    import ChevronDownIcon from "@lucide/svelte/icons/chevron-down";
    import {dndzone} from 'svelte-dnd-action';
    import type {FormEntryType, ProblemForm} from "./types";
    import {recalculateSortIndexes} from "./sortIndexUtils";
    import type {Snippet} from 'svelte';

    interface Props {
        title: string;
        entries: FormEntryType[];
        form: ProblemForm;
        category: 'dtEntries' | 'styleEntries' | 'penaltyEntries';
        children: Snippet<[{ item: FormEntryType; index: number; items: FormEntryType[] }]>;
        addButtons: Snippet<[]>;
    }

    let {title, entries, form = $bindable(), category, children, addButtons}: Props = $props();
    let isOpen = $state(true);
    let items = $state(entries ?? []);

    $effect(() => {
        items = entries ?? [];
    });

    function handleSort(e: CustomEvent) {
        items = e.detail.items;
        form[category] = recalculateSortIndexes(items);
    }
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
                <div
                    use:dndzone={{ items }}
                    onconsider={handleSort}
                    onfinalize={handleSort}
                    class="flex flex-col gap-4"
                >
                    {#each items as item, index (item.id ?? item)}
                        {@render children({ item, index, items })}
                    {/each}
                </div>
                <div class="flex gap-2 flex-wrap">
                    {@render addButtons()}
                </div>
            </Card.Content>
        </Collapsible.Content>
    </Card.Root>
</Collapsible.Root>

