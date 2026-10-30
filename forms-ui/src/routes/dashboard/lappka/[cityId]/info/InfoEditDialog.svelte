<script lang="ts">
    import {Button} from "$lib/components/ui/button/index.js";
    import * as Dialog from "$lib/components/ui/dialog/index.js";
    import InfoForm from "./InfoForm.svelte";
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
</script>

<Dialog.Root bind:open>
    <Dialog.Content class="sm:max-w-3xl max-h-[90vh] overflow-y-auto">
        <Dialog.Header>
            <Dialog.Title>{editingInfo ? 'Edytuj informację' : 'Nowa informacja'}</Dialog.Title>
            <Dialog.Description>
                {category ? `Kategoria: ${category.name}` : ''}
            </Dialog.Description>
        </Dialog.Header>

        <div class="py-4">
            <InfoForm
                    bind:name
                    bind:text
                    bind:icon
                    bind:color
                    fallbackColor={category?.color ?? null}
                    inheritColorLabel="Z kategorii"
                    editorKey={editingInfo?.id ?? 'new'}
            />
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
