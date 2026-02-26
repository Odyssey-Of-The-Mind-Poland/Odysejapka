<script lang="ts">
    import {page} from "$app/state";
    import {onMount, onDestroy} from "svelte";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Spinner} from "$lib/components/ui/spinner";
    import DownloadIcon from "@lucide/svelte/icons/download";
    import ArrowLeftIcon from "@lucide/svelte/icons/arrow-left";
    import FileTextIcon from "@lucide/svelte/icons/file-text";

    let performanceId = $derived(Number(page.params.performanceId));
    let performanceIdParam = $derived(page.params.performanceId);
    let pdfUrl = $state<string | null>(null);
    let isLoading = $state(true);
    let error = $state<string | null>(null);

    onMount(async () => {
        setBreadcrumbs([
            {name: 'Drużyny', href: '/dashboard/teams'},
            {name: `Drużyna ${performanceId}`, href: `/dashboard/teams/${performanceId}`},
            {name: 'Podgląd', href: `/dashboard/teams/${performanceId}/preview`}
        ]);

        try {
            // Route through BFF proxy — token is added server-side
            const url = `/api/proxy/v1/form/${performanceIdParam}/preview/pdf`;
            const response = await fetch(url);

            if (!response.ok) {
                throw new Error(`Failed to load PDF: ${response.statusText}`);
            }

            const blob = await response.blob();
            const objectUrl = URL.createObjectURL(blob);
            pdfUrl = objectUrl;
            isLoading = false;
        } catch (e) {
            error = e instanceof Error ? e.message : 'Failed to load PDF';
            isLoading = false;
        }
    });

    onDestroy(() => {
        if (pdfUrl) {
            URL.revokeObjectURL(pdfUrl);
        }
    });

    function downloadPdf() {
        const link = document.createElement('a');
        link.href = `/api/proxy/v1/form/${performanceIdParam}/preview/pdf/download`;
        link.download = `team-form-${performanceIdParam}.pdf`;
        link.click();
    }
</script>

<div class="flex flex-col gap-6 h-full">
    <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
        <div class="flex items-start gap-4">
            <div class="flex items-center justify-center size-12 rounded-xl bg-primary/10 shrink-0">
                <FileTextIcon class="size-6 text-primary" />
            </div>
            <div>
                <h1 class="text-2xl font-semibold tracking-tight">Podgląd formularza</h1>
                <p class="text-sm text-muted-foreground">Drużyna {performanceId}</p>
            </div>
        </div>
        <div class="flex items-center gap-2 shrink-0">
            <Button variant="outline" size="sm" onclick={downloadPdf}>
                <DownloadIcon class="size-4" />
                Pobierz PDF
            </Button>
            <Button size="sm" onclick={() => window.history.back()}>
                <ArrowLeftIcon class="size-4" />
                Powrót
            </Button>
        </div>
    </div>

    <div class="flex-1 rounded-xl border bg-card shadow-sm overflow-hidden h-full">
        {#if isLoading}
            <div class="flex flex-col items-center justify-center h-full gap-3">
                <Spinner size="sm"/>
                <p class="text-sm text-muted-foreground">Ładowanie podglądu...</p>
            </div>
        {:else if error}
            <div class="flex flex-col items-center justify-center h-full gap-3">
                <div class="flex items-center justify-center size-12 rounded-xl bg-destructive/10">
                    <FileTextIcon class="size-6 text-destructive" />
                </div>
                <div class="text-center">
                    <p class="font-medium text-destructive">Błąd ładowania PDF</p>
                    <p class="text-sm text-muted-foreground mt-1">{error}</p>
                </div>
            </div>
        {:else if pdfUrl}
            <iframe
                src={pdfUrl}
                class="w-full h-full border-0"
                title="Team Form Preview"
            ></iframe>
        {/if}
    </div>
</div>
