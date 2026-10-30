<script lang="ts">
    import {Input} from "$lib/components/ui/input/index.js";
    import TiptapEditor from "$lib/components/TiptapEditor.svelte";
    import CategoryStylePicker from "$lib/components/CategoryStylePicker.svelte";
    import InfoTilePreview from "$lib/components/InfoTilePreview.svelte";
    import {DEFAULT_CATEGORY_ICON} from "$lib/categoryStyle";
    import * as Collapsible from "$lib/components/ui/collapsible/index.js";
    import ChevronDownIcon from "@lucide/svelte/icons/chevron-down";

    let {
        name = $bindable(''),
        text = $bindable(''),
        icon = $bindable<string | null>(null),
        color = $bindable<string | null>(null),
        fallbackColor = null,
        inheritColorLabel,
        editorKey,
    }: {
        name: string;
        text: string;
        icon: string | null;
        color: string | null;
        /** Colour the tile gets while it has none of its own (the category's, when there is one). */
        fallbackColor?: string | null;
        inheritColorLabel: string;
        /** Remounts the content editor when another info is edited. */
        editorKey: string | number;
    } = $props();

    // The tile style is rarely changed, so the picker stays folded until asked for.
    let styleOpen = $state(false);

    // An unset icon is the one global default.
    let shownIcon = $derived(icon ?? DEFAULT_CATEGORY_ICON);
    let shownColor = $derived(color ?? fallbackColor);
</script>

<div class="flex flex-col gap-4">
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
                    inheritLabel={inheritColorLabel}
                    inheritIconLabel="Domyślna"
                    showPreview={false}
            />
        </Collapsible.Content>
    </Collapsible.Root>

    <div>
        <p class="text-sm font-medium mb-1">Treść</p>
        {#key editorKey}
            <TiptapEditor bind:value={text} placeholder="Treść informacji..."/>
        {/key}
    </div>
</div>
