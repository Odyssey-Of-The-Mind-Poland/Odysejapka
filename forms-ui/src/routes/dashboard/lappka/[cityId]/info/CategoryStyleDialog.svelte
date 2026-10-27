<script lang="ts">
    import {Button} from "$lib/components/ui/button/index.js";
    import * as Dialog from "$lib/components/ui/dialog/index.js";
    import CategoryStylePicker from "$lib/components/CategoryStylePicker.svelte";
    import {CATEGORY_COLORS, DEFAULT_CATEGORY_ICON} from "$lib/categoryStyle";
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

    let icon = $state<string | null>(DEFAULT_CATEGORY_ICON);
    let color = $state<string | null>(CATEGORY_COLORS[0].key);

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
            <Dialog.Title>Domyślny wygląd kategorii</Dialog.Title>
            <Dialog.Description>
                {category?.name ?? ''} — używany przez kafelki tej kategorii, które nie mają własnej ikony i koloru.
            </Dialog.Description>
        </Dialog.Header>

        <CategoryStylePicker bind:icon bind:color label={category?.name ?? ''}/>

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
