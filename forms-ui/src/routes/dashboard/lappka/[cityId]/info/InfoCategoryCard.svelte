<script lang="ts">
    import {Button} from "$lib/components/ui/button/index.js";
    import * as Card from "$lib/components/ui/card/index.js";
    import IconPlus from "@tabler/icons-svelte/icons/plus";
    import IconTrash from "@tabler/icons-svelte/icons/trash";
    import IconGripVertical from "@tabler/icons-svelte/icons/grip-vertical";
    import CategoryIcon from "$lib/components/CategoryIcon.svelte";
    import {DEFAULT_CATEGORY_ICON} from "$lib/categoryStyle";
    import {categoryColor} from "$lib/categoryStyle";
    import {dndzone} from "svelte-dnd-action";
    import type {Info, InfoCategory} from "./types";

    let {
        category,
        items = $bindable([]),
        deleting,
        oncreate,
        onedit,
        ondelete,
        onsort,
        onfinalize,
    }: {
        category: InfoCategory;
        items: Info[];
        deleting: boolean;
        oncreate: () => void;
        onedit: (info: Info) => void;
        ondelete: (info: Info) => void;
        onsort: (e: CustomEvent) => void;
        onfinalize: (e: CustomEvent) => void;
    } = $props();

    let accent = $derived(categoryColor(category.color));

    function handleDelete(e: Event, info: Info) {
        e.stopPropagation();
        ondelete(info);
    }
</script>

<Card.Root>
    <Card.Header>
        <div class="flex items-center justify-between">
            <div class="flex items-center gap-3 min-w-0">
                <div
                        class="flex size-9 shrink-0 items-center justify-center rounded-full"
                        style="background:{accent.background};color:{accent.foreground}"
                >
                    <CategoryIcon name={DEFAULT_CATEGORY_ICON} class="size-5"/>
                </div>
                <Card.Title class="truncate">{category.name}</Card.Title>
            </div>
            <div class="flex gap-2">
                <Button variant="outline" size="sm" onclick={oncreate}>
                    <IconPlus class="size-4 mr-1"/>
                    Dodaj
                </Button>
            </div>
        </div>
        <Card.Description>{items.length} {items.length === 1 ? 'wpis' : 'wpisów'}</Card.Description>
    </Card.Header>
    <Card.Content>
        {#if items.length === 0}
            <p class="text-sm text-muted-foreground py-4 text-center">Brak informacji w tej kategorii</p>
        {:else}
            <div
                use:dndzone={{ items, flipDurationMs: 200 }}
                onconsider={onsort}
                onfinalize={onfinalize}
                class="flex flex-col"
            >
                {#each items as info (info.id)}
                    <button
                        class="flex items-center gap-3 py-3 px-2 -mx-2 rounded-md text-left hover:bg-muted/50 transition-colors cursor-pointer group"
                        onclick={() => onedit(info)}
                    >
                        <div class="flex items-center text-muted-foreground/40 cursor-grab active:cursor-grabbing">
                            <IconGripVertical class="size-4"/>
                        </div>
                        <div
                                class="flex size-7 shrink-0 items-center justify-center rounded-full"
                                style="background:{categoryColor(info.color ?? category.color).background};color:{categoryColor(info.color ?? category.color).foreground}"
                        >
                            <CategoryIcon name={info.icon} class="size-4"/>
                        </div>
                        <div class="flex-1 min-w-0">
                            <p class="font-medium text-sm truncate">{info.infoName}</p>
                        </div>
                        <div class="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                            <Button
                                    variant="ghost"
                                    size="icon"
                                    class="size-8"
                                    onclick={(e) => handleDelete(e, info)}
                                    disabled={deleting}
                            >
                                <IconTrash class="size-4 text-red-500"/>
                            </Button>
                        </div>
                    </button>
                {/each}
            </div>
        {/if}
    </Card.Content>
</Card.Root>
