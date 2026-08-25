<script lang="ts">
    import {page} from '$app/state';
    import {downloadCsvFile, downloadRakLatexPdf, downloadRakShortPdf} from '$lib/zwierzyniec';
    import {createOdysejaQuery} from '$lib/queries';
    import {Input} from '$lib/components/ui/input';
    import {Label} from '$lib/components/ui/label';
    import {Spinner} from '$lib/components/ui/spinner';
    import {toast} from 'svelte-sonner';
    import * as Card from '$lib/components/ui/card';

    let cityId = $derived(Number(page.params.cityId));

    let rakQuery = $derived(
        createOdysejaQuery<{ zspId: string; contestName?: string }>({
            queryKey: ['rak', String(cityId)],
            path: `/api/v1/rak?cityId=${cityId}`
        })
    );

    let zspId = $state('');
    let contestName = $state('');
    let isRegion = $state(false);
    let isLoading = $state(false);

    $effect(() => {
        const d = rakQuery.data;
        if (d) {
            zspId = d.zspId ?? '';
            contestName = d.contestName ?? '';
        }
    });

    async function downloadFile(
        fn: (cityId: number, zspId: string, isRegion: boolean, contestName?: string) => Promise<ArrayBuffer>,
        extension: string
    ) {
        try {
            isLoading = true;
            const content = await fn(cityId, zspId, isRegion, contestName || undefined);
            const blob = new Blob([content], {type: 'application/pdf'});
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            const safeName = (contestName ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/ /g, '_');
            console.log(safeName)
            a.download = `wyniki_${safeName}.${extension}`;
            a.style.display = 'none';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
            toast.success('PDF wygenerowany');
        } catch (e) {
            toast.error(e instanceof Error ? e.message : 'Błąd generowania');
        } finally {
            isLoading = false;
        }
    }

    const downloadShortPdf = () => downloadFile(downloadRakShortPdf, 'pdf');
    const downloadCsv = () => downloadFile(downloadCsvFile, 'csv');
    const downloadLatexPdf = () => downloadFile(downloadRakLatexPdf, 'pdf');
</script>

<Card.Root>
    <Card.Header>
        <Card.Title>Rankingowy Analizator Końcowy</Card.Title>
        <Card.Description>Generowanie rankingów i wyników w formacie PDF</Card.Description>
    </Card.Header>
    <Card.Content>
        {#if rakQuery.isPending}
            <div class="flex items-center gap-3 py-4">
                <Spinner size="sm"/>
                <span class="text-muted-foreground">Ładowanie...</span>
            </div>
        {:else if rakQuery.error}
            <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-4">
                <p class="text-sm text-destructive">{String(rakQuery.error)}</p>
            </div>
        {:else}
            <div class="flex flex-col gap-6">
                <div class="grid gap-4 max-w-md">
                    <div class="space-y-2">
                        <Label for="zspId">ZSP ID</Label>
                        <Input id="zspId" type="text" bind:value={zspId} placeholder="ZSP ID"/>
                    </div>
                    <div class="space-y-2">
                        <Label for="contestName">Nazwa konkursu</Label>
                        <Input id="contestName" type="text" bind:value={contestName} placeholder="Nazwa konkursu"/>
                    </div>
                    <div class="flex items-center gap-2">
                        <input type="checkbox" id="isRegion" bind:checked={isRegion} class="rounded"/>
                        <Label for="isRegion">Region (FR)</Label>
                    </div>
                </div>

                <div class="grid gap-3 sm:grid-cols-3">
                    <button
                            class="flex flex-col gap-1 rounded-lg border p-4 text-left transition-colors hover:bg-accent disabled:opacity-50"
                            onclick={downloadLatexPdf}
                            disabled={isLoading}
                    >
                        <span class="text-sm font-medium">Pełne wyniki PDF</span>
                        <span class="text-xs text-muted-foreground">Pełne wyniki</span>
                    </button>
                    <button
                            class="flex flex-col gap-1 rounded-lg border p-4 text-left transition-colors hover:bg-accent disabled:opacity-50"
                            onclick={downloadShortPdf}
                            disabled={isLoading}
                    >
                        <span class="text-sm font-medium">Skrócony PDF</span>
                        <span class="text-xs text-muted-foreground">Skrócone wyniki na ceremonie zakończęcia</span>
                    </button>
                    <button
                            class="flex flex-col gap-1 rounded-lg border p-4 text-left transition-colors hover:bg-accent disabled:opacity-50"
                            onclick={downloadCsv}
                            disabled={isLoading}
                    >
                        <span class="text-sm font-medium">CSV</span>
                        <span class="text-xs text-muted-foreground">Generuj CSV z wynikami</span>
                    </button>
                </div>

                {#if isLoading}
                    <div class="flex items-center gap-2 text-muted-foreground">
                        <Spinner size="sm"/>
                        <span class="text-sm">Generowanie...</span>
                    </div>
                {/if}
            </div>
        {/if}
    </Card.Content>
</Card.Root>
