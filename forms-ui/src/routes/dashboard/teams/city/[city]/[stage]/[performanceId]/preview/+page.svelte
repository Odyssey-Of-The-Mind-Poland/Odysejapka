<script lang="ts">
    import {page} from "$app/state";
    import {onMount, onDestroy} from "svelte";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Spinner} from "$lib/components/ui/spinner";
    import DownloadIcon from "@lucide/svelte/icons/download";
    import ArrowLeftIcon from "@lucide/svelte/icons/arrow-left";
    import FileTextIcon from "@lucide/svelte/icons/file-text";

    let cityName = $derived(decodeURIComponent(page.params.city));
    let stageParam = $derived(page.params.stage);
    let performanceId = $derived(Number(page.params.performanceId));
    let performanceIdParam = $derived(page.params.performanceId);

    let stageUrl = $derived(`/dashboard/teams/city/${encodeURIComponent(cityName)}/${stageParam}`);
    let teamUrl = $derived(`${stageUrl}/${performanceIdParam}`);

    let activeTab = $state<'polish' | 'english'>('polish');
    let pdfUrl = $state<string | null>(null);
    let isLoading = $state(true);
    let error = $state<string | null>(null);

    function getPdfEndpoint(lang: 'polish' | 'english'): string {
        if (lang === 'english') {
            return `/api/proxy/v1/form/${performanceIdParam}/preview/pdf/english`;
        }
        return `/api/proxy/v1/form/${performanceIdParam}/preview/pdf`;
    }

    async function loadPdf(lang: 'polish' | 'english') {
        isLoading = true;
        error = null;

        if (pdfUrl) {
            URL.revokeObjectURL(pdfUrl);
            pdfUrl = null;
        }

        try {
            const url = getPdfEndpoint(lang);
            const response = await fetch(url);

            if (!response.ok) {
                throw new Error(`Failed to load PDF: ${response.statusText}`);
            }

            const blob = await response.blob();
            pdfUrl = URL.createObjectURL(blob);
            isLoading = false;
        } catch (e) {
            error = e instanceof Error ? e.message : 'Failed to load PDF';
            isLoading = false;
        }
    }

    function switchTab(tab: 'polish' | 'english') {
        if (tab === activeTab) return;
        activeTab = tab;
        loadPdf(tab);
    }

    onMount(async () => {
        setBreadcrumbs([
            {name: 'Drużyny', href: '/dashboard/teams'},
            {name: cityName, href: `/dashboard/teams/city/${encodeURIComponent(cityName)}`},
            {name: `Scena ${stageParam}`, href: stageUrl},
            {name: `Drużyna ${performanceId}`, href: teamUrl},
            {name: 'Podgląd', href: `${teamUrl}/preview`}
        ]);

        await loadPdf('polish');
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

    <div class="flex gap-1 rounded-lg bg-muted p-1 w-fit">
        <button
            class="rounded-md px-3 py-1.5 text-sm font-medium transition-colors no-underline cursor-pointer
                {activeTab === 'polish'
                    ? 'bg-background text-foreground shadow-sm'
                    : 'text-muted-foreground hover:text-foreground'}"
            onclick={() => switchTab('polish')}
        >
            Polski
        </button>
        <button
            class="rounded-md px-3 py-1.5 text-sm font-medium transition-colors no-underline cursor-pointer
                {activeTab === 'english'
                    ? 'bg-background text-foreground shadow-sm'
                    : 'text-muted-foreground hover:text-foreground'}"
            onclick={() => switchTab('english')}
        >
            Angielski
        </button>
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
