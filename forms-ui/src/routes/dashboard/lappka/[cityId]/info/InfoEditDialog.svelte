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
    <Dialog.Content class="sm:max-w-6xl max-h-[90vh] overflow-y-auto">
        <!-- The save button sits in the header: the editor with its preview is tall, so a footer would be off screen. -->
        <Dialog.Header class="sticky top-0 z-10 -mx-4 -mt-4 flex flex-row items-start justify-between gap-4 border-b bg-background px-4 pb-4 pt-4 pr-14 sm:-mx-6 sm:-mt-6 sm:px-6 sm:pt-6 sm:pr-14">
            <div class="flex flex-col gap-1.5 text-left">
                <Dialog.Title>{editingInfo ? 'Edytuj informację' : 'Nowa informacja'}</Dialog.Title>
                <Dialog.Description>
                    {category ? `Kategoria: ${category.name}` : ''}
                </Dialog.Description>
            </div>
            <div class="flex shrink-0 gap-2">
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
            </div>
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
    </Dialog.Content>
</Dialog.Root>
