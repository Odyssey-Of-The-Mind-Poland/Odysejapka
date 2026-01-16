<script lang="ts">
    import {page} from "$app/state";
    import {onMount, onDestroy} from "svelte";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Spinner} from "$lib/components/ui/spinner";
    import {session as sessionStore} from "$lib/sessionStore";
    import {get} from "svelte/store";

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
            const currentSession = get(sessionStore);
            const token = currentSession?.accessToken;
            
            const url = `/api/v1/form/${performanceIdParam}/preview/pdf`;
            const headers: HeadersInit = {};
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
            
            const response = await fetch(url, { headers });
            
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
        link.href = `/api/v1/form/${performanceIdParam}/preview/pdf/download`;
        link.download = `team-form-${performanceIdParam}.pdf`;
        link.click();
    }
</script>

<div class="flex flex-col gap-4 h-full">
    <div class="flex justify-between items-center">
        <h1 class="text-2xl font-bold">Podgląd formularza drużyny</h1>
        <div class="flex gap-2">
            <Button variant="outline" onclick={downloadPdf}>
                Pobierz PDF
            </Button>
            <Button onclick={() => window.history.back()}>
                Powrót
            </Button>
        </div>
    </div>

    <div class="flex-1 border rounded-lg overflow-hidden h-full">
        {#if isLoading}
            <div class="flex justify-center items-center h-full">
                <Spinner size="sm"/>
            </div>
        {:else if error}
            <div class="flex justify-center items-center h-full text-red-500">
                <div class="text-center">
                    <p class="font-medium">Błąd ładowania PDF</p>
                    <p class="text-sm">{error}</p>
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

