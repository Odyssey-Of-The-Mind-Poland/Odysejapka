<script lang="ts">
    import * as Dialog from '$lib/components/ui/dialog/index.js';
    import {Button} from '$lib/components/ui/button/index.js';
    import {Spinner} from '$lib/components/ui/spinner';
    import IconKey from '@tabler/icons-svelte/icons/key';
    import IconCopy from '@tabler/icons-svelte/icons/copy';
    import {apiFetch} from '$lib/api';
    import {toast} from 'svelte-sonner';

    let {cityId, stage}: { cityId: number; stage: number } = $props();

    type Credentials = {
        email: string;
        password: string;
    };

    let open = $state(false);
    let loading = $state(false);
    let credentials = $state<Credentials | null>(null);
    let error = $state<string | null>(null);

    async function fetchCredentials() {
        loading = true;
        error = null;
        try {
            credentials = await apiFetch<Credentials>(`/api/stage/${cityId}/${stage}/credentials`);
        } catch (e) {
            error = 'Brak danych logowania dla tej sceny.';
            credentials = null;
        } finally {
            loading = false;
        }
    }

    function handleOpen(isOpen: boolean) {
        open = isOpen;
        if (isOpen) {
            fetchCredentials();
        }
    }

    async function copyToClipboard(text: string) {
        await navigator.clipboard.writeText(text);
        toast.success('Skopiowano do schowka');
    }
</script>

<Dialog.Root open={open} onOpenChange={handleOpen}>
    <Dialog.Trigger>
        {#snippet child({ props })}
            <Button {...props} variant="outline" size="sm">
                <IconKey class="size-4 mr-1"/>
                Dane logowania
            </Button>
        {/snippet}
    </Dialog.Trigger>
    <Dialog.Content class="sm:max-w-md">
        <Dialog.Header>
            <Dialog.Title>Dane logowania — Scena {stage}</Dialog.Title>
            <Dialog.Description>
                Poświadczenia użytkownika przypisanego do tej sceny.
            </Dialog.Description>
        </Dialog.Header>

        <div class="py-4">
            {#if loading}
                <div class="flex flex-col items-center justify-center py-8 gap-3">
                    <Spinner size="sm"/>
                    <p class="text-sm text-muted-foreground">Pobieranie...</p>
                </div>
            {:else if error}
                <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-4 text-center">
                    <p class="text-sm text-destructive">{error}</p>
                </div>
            {:else if credentials}
                <div class="space-y-4">
                    <div>
                        <span class="text-sm font-medium text-muted-foreground">Email</span>
                        <div class="flex items-center gap-2 mt-1">
                            <code class="flex-1 rounded-md bg-muted px-3 py-2 text-sm font-mono">
                                {credentials.email}
                            </code>
                            <Button
                                    variant="ghost"
                                    size="icon"
                                    onclick={() => copyToClipboard(credentials!.email)}
                            >
                                <IconCopy class="size-4"/>
                            </Button>
                        </div>
                    </div>
                    <div>
                        <span class="text-sm font-medium text-muted-foreground">Hasło</span>
                        <div class="flex items-center gap-2 mt-1">
                            <code class="flex-1 rounded-md bg-muted px-3 py-2 text-sm font-mono">
                                {credentials.password}
                            </code>
                            <Button
                                    variant="ghost"
                                    size="icon"
                                    onclick={() => copyToClipboard(credentials!.password)}
                            >
                                <IconCopy class="size-4"/>
                            </Button>
                        </div>
                    </div>
                </div>
            {/if}
        </div>

        <Dialog.Footer>
            <Dialog.Close>
                {#snippet child({ props })}
                    <Button {...props} variant="outline">Zamknij</Button>
                {/snippet}
            </Dialog.Close>
        </Dialog.Footer>
    </Dialog.Content>
</Dialog.Root>
