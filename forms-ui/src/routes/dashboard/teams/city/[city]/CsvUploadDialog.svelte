<script lang="ts">
    import * as Dialog from '$lib/components/ui/dialog/index.js';
    import {Button} from '$lib/components/ui/button/index.js';
    import {Spinner} from '$lib/components/ui/spinner';
    import IconUpload from '@tabler/icons-svelte/icons/upload';
    import IconFileTypeCsv from '@tabler/icons-svelte/icons/file-type-csv';
    import IconX from '@tabler/icons-svelte/icons/x';
    import {toast} from 'svelte-sonner';
    import {getQueryClientContext} from '@tanstack/svelte-query';

    let {cityId}: { cityId: number } = $props();

    let open = $state(false);
    let file = $state<File | null>(null);
    let uploading = $state(false);
    let dragging = $state(false);

    const queryClient = getQueryClientContext();

    function handleDrop(e: DragEvent) {
        e.preventDefault();
        dragging = false;
        const dropped = e.dataTransfer?.files?.[0];
        if (dropped && dropped.name.endsWith('.csv')) {
            file = dropped;
        } else {
            toast.error('Wybierz plik CSV');
        }
    }

    function handleDragOver(e: DragEvent) {
        e.preventDefault();
        dragging = true;
    }

    function handleDragLeave() {
        dragging = false;
    }

    function handleFileInput(e: Event) {
        const input = e.target as HTMLInputElement;
        const selected = input.files?.[0];
        if (selected) {
            file = selected;
        }
    }

    function clearFile() {
        file = null;
    }

    async function handleUpload() {
        if (!file) return;

        uploading = true;
        try {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('cityId', String(cityId));

            // Route through BFF proxy — token is added server-side
            const response = await fetch('/api/proxy/timeTable/csv', {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) {
                throw new Error(`${response.status} ${response.statusText}`);
            }

            toast.success('Drużyny zostały zaimportowane');
            await queryClient.invalidateQueries({queryKey: ['performanceGroups']});
            open = false;
            file = null;
        } catch (err) {
            toast.error(`Błąd importu: ${err instanceof Error ? err.message : 'Nieznany błąd'}`);
        } finally {
            uploading = false;
        }
    }
</script>

<Dialog.Root bind:open>
    <Dialog.Trigger>
        {#snippet child({ props })}
            <Button {...props} variant="outline" size="sm">
                <IconUpload class="size-4 mr-1"/>
                Importuj CSV
            </Button>
        {/snippet}
    </Dialog.Trigger>
    <Dialog.Content class="sm:max-w-md">
        <Dialog.Header>
            <Dialog.Title>Importuj drużyny z CSV</Dialog.Title>
            <Dialog.Description>
                Przeciągnij plik CSV lub wybierz go z dysku.
            </Dialog.Description>
        </Dialog.Header>

        <div class="py-4">
            {#if uploading}
                <div class="flex flex-col items-center justify-center py-12 gap-3">
                    <Spinner size="sm"/>
                    <p class="text-sm text-muted-foreground">Importowanie drużyn...</p>
                </div>
            {:else if file}
                <div class="flex items-center gap-3 rounded-lg border p-4">
                    <IconFileTypeCsv class="size-8 text-green-600 shrink-0"/>
                    <div class="flex-1 min-w-0">
                        <p class="text-sm font-medium truncate">{file.name}</p>
                        <p class="text-xs text-muted-foreground">
                            {(file.size / 1024).toFixed(1)} KB
                        </p>
                    </div>
                    <Button variant="ghost" size="icon" onclick={clearFile}>
                        <IconX class="size-4"/>
                    </Button>
                </div>
            {:else}
                <button
                        class="w-full rounded-lg border-2 border-dashed p-8 text-center transition-colors
                            {dragging ? 'border-primary bg-primary/5' : 'border-muted-foreground/25 hover:border-muted-foreground/50'}"
                        ondrop={handleDrop}
                        ondragover={handleDragOver}
                        ondragleave={handleDragLeave}
                        onclick={() => document.getElementById('csv-file-input')?.click()}
                >
                    <IconUpload class="size-8 text-muted-foreground/50 mx-auto mb-2"/>
                    <p class="text-sm text-muted-foreground font-medium">
                        Przeciągnij plik CSV tutaj
                    </p>
                    <p class="text-xs text-muted-foreground/70 mt-1">
                        lub kliknij, aby wybrać
                    </p>
                </button>
                <input
                        id="csv-file-input"
                        type="file"
                        accept=".csv"
                        class="hidden"
                        onchange={handleFileInput}
                />
            {/if}
        </div>

        <Dialog.Footer>
            <Dialog.Close>
                {#snippet child({ props })}
                    <Button {...props} variant="outline" disabled={uploading}>Anuluj</Button>
                {/snippet}
            </Dialog.Close>
            <Button onclick={handleUpload} disabled={!file || uploading}>
                {#if uploading}
                    Importowanie...
                {:else}
                    Importuj
                {/if}
            </Button>
        </Dialog.Footer>
    </Dialog.Content>
</Dialog.Root>
