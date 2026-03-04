<script lang="ts">
    import {Button} from "$lib/components/ui/button/index.js";
    import {Input} from "$lib/components/ui/input/index.js";
    import * as Dialog from "$lib/components/ui/dialog/index.js";
    import TiptapEditor from "$lib/components/TiptapEditor.svelte";
    import type {Info, InfoCategory} from "./types";

    let {
        open = $bindable(false),
        editingInfo,
        category,
        name = $bindable(''),
        text = $bindable(''),
        saving,
        onsave,
    }: {
        open: boolean;
        editingInfo: Info | null;
        category: InfoCategory | null;
        name: string;
        text: string;
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

        <div class="flex flex-col gap-4 py-4">
            <div>
                <label class="text-sm font-medium mb-1 block">Nazwa</label>
                <Input bind:value={name} placeholder="Nazwa informacji..."/>
            </div>

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
