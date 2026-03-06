<script lang="ts">
    import {Button} from "$lib/components/ui/button/index.js";
    import {Input} from "$lib/components/ui/input/index.js";
    import * as Dialog from "$lib/components/ui/dialog/index.js";

    type Props = {
        open: boolean;
        saving: boolean;
        onsave: (name: string) => void;
    };

    let {open = $bindable(false), saving, onsave}: Props = $props();

    let roomName = $state('');

    function handleSave() {
        if (!roomName.trim()) return;
        onsave(roomName.trim());
        roomName = '';
    }
</script>

<Dialog.Root bind:open>
    <Dialog.Content class="sm:max-w-sm">
        <Dialog.Header>
            <Dialog.Title>Nowy pokoj spontaniczny</Dialog.Title>
        </Dialog.Header>

        <div class="py-4">
            <label class="text-sm font-medium mb-1 block">Nazwa pokoju</label>
            <Input bind:value={roomName} placeholder="np. Pokoj 1"/>
        </div>

        <Dialog.Footer>
            <Dialog.Close>
                {#snippet child({ props })}
                    <Button {...props} variant="outline">Anuluj</Button>
                {/snippet}
            </Dialog.Close>
            <Button onclick={handleSave} disabled={!roomName.trim() || saving}>
                Dodaj
            </Button>
        </Dialog.Footer>
    </Dialog.Content>
</Dialog.Root>
