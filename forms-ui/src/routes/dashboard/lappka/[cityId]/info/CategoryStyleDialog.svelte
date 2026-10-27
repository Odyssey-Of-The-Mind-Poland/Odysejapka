<script lang="ts">
    import {Button} from "$lib/components/ui/button/index.js";
    import * as Dialog from "$lib/components/ui/dialog/index.js";
    import CategoryIcon from "$lib/components/CategoryIcon.svelte";
    import {CATEGORY_COLORS, CATEGORY_ICONS, DEFAULT_CATEGORY_ICON, categoryColor} from "$lib/categoryStyle";
    import type {InfoCategory} from "./types";

    let {
        open = $bindable(false),
        category,
        saving,
        onsave,
    }: {
        open: boolean;
        category: InfoCategory | null;
        saving: boolean;
        onsave: (category: InfoCategory) => void;
    } = $props();

    let icon = $state(DEFAULT_CATEGORY_ICON);
    let color = $state<string>(CATEGORY_COLORS[0].key);
    let preview = $derived(categoryColor(color));

    // Re-seed the form whenever the dialog is opened for another category.
    $effect(() => {
        if (open && category) {
            icon = category.icon ?? DEFAULT_CATEGORY_ICON;
            color = category.color ?? CATEGORY_COLORS[0].key;
        }
    });

    function save() {
        if (!category) return;
        onsave({...category, icon, color});
    }
</script>

<Dialog.Root bind:open>
    <Dialog.Content class="sm:max-w-xl max-h-[90vh] overflow-y-auto">
        <Dialog.Header>
            <Dialog.Title>Wygląd kategorii</Dialog.Title>
            <Dialog.Description>
                {category?.name ?? ''} — ikona i kolor kafelka w aplikacji Łappka Omera.
            </Dialog.Description>
        </Dialog.Header>

        <div class="flex flex-col gap-5 py-2">
            <div class="flex items-center gap-3">
                <div
                        class="flex h-20 w-44 items-end rounded-2xl border-2 p-4 text-sm font-medium"
                        style="background:{preview.background};border-color:{preview.foreground}33;color:{preview.foreground}"
                >
                    {category?.name ?? ''}
                </div>
                <div
                        class="flex size-12 items-center justify-center rounded-full"
                        style="background:{preview.background};color:{preview.foreground}"
                >
                    <CategoryIcon name={icon} class="size-6"/>
                </div>
            </div>

            <div>
                <p class="text-sm font-medium mb-2">Kolor</p>
                <div class="flex flex-wrap gap-2" role="radiogroup" aria-label="Kolor kategorii">
                    {#each CATEGORY_COLORS as option (option.key)}
                        <button
                                type="button"
                                role="radio"
                                aria-checked={color === option.key}
                                aria-label={option.label}
                                title={option.label}
                                class="size-9 rounded-full border-2 transition-shadow {color === option.key ? 'ring-2 ring-offset-2 ring-foreground' : ''}"
                                style="background:{option.background};border-color:{option.foreground}"
                                onclick={() => (color = option.key)}
                        ></button>
                    {/each}
                </div>
            </div>

            <div>
                <p class="text-sm font-medium mb-2">Ikona</p>
                <div class="grid grid-cols-8 gap-1.5 max-h-56 overflow-y-auto pr-1" role="radiogroup" aria-label="Ikona kategorii">
                    {#each Object.keys(CATEGORY_ICONS) as key (key)}
                        <button
                                type="button"
                                role="radio"
                                aria-checked={icon === key}
                                aria-label={key}
                                title={key}
                                class="flex size-10 items-center justify-center rounded-md border hover:bg-muted {icon === key ? 'border-foreground bg-muted' : 'border-transparent'}"
                                onclick={() => (icon = key)}
                        >
                            <CategoryIcon name={key} class="size-5"/>
                        </button>
                    {/each}
                </div>
            </div>
        </div>

        <Dialog.Footer>
            <Dialog.Close>
                {#snippet child({ props })}
                    <Button {...props} variant="outline">Anuluj</Button>
                {/snippet}
            </Dialog.Close>
            <Button onclick={save} disabled={saving || !category}>Zapisz</Button>
        </Dialog.Footer>
    </Dialog.Content>
</Dialog.Root>
