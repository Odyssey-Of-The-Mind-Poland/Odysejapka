<script lang="ts">
    import {Button} from "$lib/components/ui/button/index.js";
    import {Input} from "$lib/components/ui/input/index.js";
    import * as Dialog from "$lib/components/ui/dialog/index.js";
    import TiptapEditor from "$lib/components/TiptapEditor.svelte";
    import CategoryStylePicker from "$lib/components/CategoryStylePicker.svelte";
    import InfoTilePreview from "$lib/components/InfoTilePreview.svelte";
    import {DEFAULT_CATEGORY_ICON} from "$lib/categoryStyle";
    import * as Collapsible from "$lib/components/ui/collapsible/index.js";
    import ChevronDownIcon from "@lucide/svelte/icons/chevron-down";
    import type {Info, InfoCategory} from "./types";

    let {
        open = $bindable(false),
        editingInfo,
        category,
        name = $bindable(''),
        text = $bindable(''),
        icon = $bindable<string | null>(null),
        color = $bindable<string | null>(null),
        saving,
        onsave,
    }: {
        open: boolean;
        editingInfo: Info | null;
        category: InfoCategory | null;
        name: string;
        text: string;
        icon: string | null;
        color: string | null;
        saving: boolean;
        onsave: () => void;
    } = $props();

    // The tile style is rarely changed, so the picker stays folded until asked for.
    let styleOpen = $state(false);
    $effect(() => {
        if (open) styleOpen = false;
    });

    // An unset icon is the shared default and an unset colour follows the category, as in the app.
    let shownIcon = $derived(icon ?? DEFAULT_CATEGORY_ICON);
    let shownColor = $derived(color ?? category?.color ?? null);
</script>

<Dialog.Root bind:open>
    <Dialog.Content class="sm:max-w-3xl max-h-[90vh] overflow-y-auto">
        <Dialog.Header>
            <Dialog.Title>{editingInfo ? 'Edytuj informację' : 'Nowa informacja'}</Dialog.Title>
            <Dialog.Description>
                {category ? `Kategoria: ${category.name}` : ''}
            </Dialog.Description>
        </Dialog.Header>

        <div class="flex flex-col gap-4 py-4">
            <Collapsible.Root bind:open={styleOpen} class="flex flex-col gap-3">
                <div>
                    <label class="text-sm font-medium mb-1 block" for="info-name">Nazwa</label>
                    <div class="flex items-center gap-2">
                        <Input id="info-name" class="flex-1" bind:value={name} placeholder="Nazwa informacji..."/>
                        <Collapsible.Trigger
                                type="button"
                                class="group flex shrink-0 items-center gap-1.5 rounded-2xl outline-none focus-visible:ring-2 focus-visible:ring-ring"
                                aria-label="Wygląd kafelka"
                                title="Wygląd kafelka"
                        >
                            <InfoTilePreview
                                    class="h-9 w-32 rounded-xl border px-2.5 py-1.5 text-xs"
                                    label={name || 'Wygląd'}
                                    icon={shownIcon}
                                    color={shownColor}
                            />
                            <ChevronDownIcon class="size-4 text-muted-foreground transition-transform group-data-[state=open]:rotate-180"/>
                        </Collapsible.Trigger>
                    </div>
                </div>

                <Collapsible.Content>
                    <CategoryStylePicker
                            bind:icon
                            bind:color
                            label={name || 'Nazwa informacji'}
                            inheritLabel="Z kategorii"
                            inheritIconLabel="Domyślna"
                            showPreview={false}
                    />
                </Collapsible.Content>
            </Collapsible.Root>

            <div>
                <label class="text-sm font-medium mb-1 block">Treść</label>
                {#key editingInfo?.id ?? 'new'}
                    <TiptapEditor bind:value={text} placeholder="Treść informacji..."/>
                {/key}
            </div>
        </div>

        <Dialog.Footer>
            <Dialog.Close>
                {#snippet child({ props })}
                    <Button {...props} variant="outline">Anuluj</Button>
                {/snippet}
            </Dialog.Close>
            <Button
                    onclick={onsave}
                    disabled={!name.trim() || saving}
            >
                {editingInfo ? 'Zapisz' : 'Dodaj'}
            </Button>
        </Dialog.Footer>
    </Dialog.Content>
</Dialog.Root>
