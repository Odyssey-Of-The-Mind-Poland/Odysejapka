<script lang="ts">
    import * as Dialog from '$lib/components/ui/dialog/index.js';
    import {Button} from '$lib/components/ui/button/index.js';
    import {Spinner} from '$lib/components/ui/spinner';
    import IconFileTypePdf from '@tabler/icons-svelte/icons/file-type-pdf';
    import {toast} from 'svelte-sonner';

    let {cityId}: { cityId: number } = $props();

    type ResultsStatus = {
        totalPerformances: number;
        unapprovedForms: string[];
        missingSpontans: string[];
    };

    let open = $state(false);
    let loading = $state(false);
    let generating = $state(false);
    let status = $state<ResultsStatus | null>(null);

    async function fetchStatus() {
        loading = true;
        status = null;
        try {
            const res = await fetch(`/api/proxy/v1/dashboard/cities/${cityId}/results/status`);
            if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
            status = await res.json();
            open = true;
        } catch (err) {
            toast.error(`Blad pobierania statusu: ${err instanceof Error ? err.message : 'Nieznany blad'}`);
        } finally {
            loading = false;
        }
    }

    async function generatePdf() {
        generating = true;
        try {
            const res = await fetch(`/api/proxy/v1/dashboard/cities/${cityId}/results/pdf`, {
                method: 'POST',
            });
            if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
            const blob = await res.blob();
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'wyniki.pdf';
            a.click();
            URL.revokeObjectURL(url);
            toast.success('Wyniki wygenerowane');
            open = false;
        } catch (err) {
            toast.error(`Blad generowania PDF: ${err instanceof Error ? err.message : 'Nieznany blad'}`);
        } finally {
            generating = false;
        }
    }

    let allOk = $derived(
        status != null && status.unapprovedForms.length === 0 && status.missingSpontans.length === 0
    );
</script>

<Button variant="outline" size="sm" onclick={fetchStatus} disabled={loading}>
    {#if loading}
        <Spinner size="sm"/>
    {:else}
        <IconFileTypePdf class="size-4 mr-1.5"/>
    {/if}
    Generuj wyniki
</Button>

<Dialog.Root bind:open>
    <Dialog.Content class="sm:max-w-lg">
        <Dialog.Header>
            <Dialog.Title>Generowanie wyników</Dialog.Title>
            <Dialog.Description>
                Sprawdz status danych przed wygenerowaniem PDF z wynikami.
            </Dialog.Description>
        </Dialog.Header>

        {#if status}
            <div class="flex flex-col gap-3 py-4">
                <p class="text-sm text-muted-foreground">
                    Liczba wystepow: <strong>{status.totalPerformances}</strong>
                </p>

                {#if status.unapprovedForms.length > 0}
                    <div class="rounded-lg border border-yellow-300 bg-yellow-50 p-3">
                        <p class="text-sm font-medium text-yellow-800">
                            Niezatwierdzone formularze DT ({status.unapprovedForms.length})
                        </p>
                        <div class="mt-2 max-h-32 overflow-y-auto">
                            <ul class="text-sm text-yellow-700 list-disc list-inside">
                                {#each status.unapprovedForms as team}
                                    <li>{team}</li>
                                {/each}
                            </ul>
                        </div>
                    </div>
                {/if}

                {#if status.missingSpontans.length > 0}
                    <div class="rounded-lg border border-yellow-300 bg-yellow-50 p-3">
                        <p class="text-sm font-medium text-yellow-800">
                            Brak wyników spontanów ({status.missingSpontans.length})
                        </p>
                        <div class="mt-2 max-h-32 overflow-y-auto">
                            <ul class="text-sm text-yellow-700 list-disc list-inside">
                                {#each status.missingSpontans as team}
                                    <li>{team}</li>
                                {/each}
                            </ul>
                        </div>
                    </div>
                {/if}

                {#if allOk}
                    <div class="rounded-lg border border-green-300 bg-green-50 p-3">
                        <p class="text-sm font-medium text-green-800">
                            Wszystkie dane sa kompletne. Mozna wygenerowac wyniki.
                        </p>
                    </div>
                {/if}
            </div>
        {/if}

        <Dialog.Footer>
            <Dialog.Close>
                {#snippet child({ props })}
                    <Button {...props} variant="outline" disabled={generating}>Anuluj</Button>
                {/snippet}
            </Dialog.Close>
            <Button onclick={generatePdf} disabled={generating || !status}>
                {#if generating}
                    <Spinner size="sm"/>
                    Generowanie...
                {:else}
                    Generuj PDF
                {/if}
            </Button>
        </Dialog.Footer>
    </Dialog.Content>
</Dialog.Root>
