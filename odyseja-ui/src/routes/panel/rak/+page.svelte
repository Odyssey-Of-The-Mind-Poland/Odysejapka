<script lang="ts">
    import { onMount, onDestroy } from 'svelte';
    import {
        generateCsv,
        generateDetailedCsv,
        generateHtmlResults,
        generatePdfResults,
        generateShortPdfResults,
        getPdfTemplate,
        savePdfTemplate
    } from './rak';
    import { ProgressRadial } from '@skeletonlabs/skeleton';

    // ✅ Monaco (Vite-friendly ESM import + worker)
    import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
    // For plaintext we only need the core editor worker:
    // @ts-ignore - Vite's ?worker loader
    import EditorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker';

    let zspId = '';
    let isLoading = false;      // for the CSV/HTML buttons row
    let isGenerating = false;   // for the PDF preview generate action

    let editorEl: HTMLDivElement;
    let editor: monaco.editor.IStandaloneCodeEditor | null = null;
    let pdfUrl: string | null = null; // preview URL

    function revokePdfUrl() {
        if (pdfUrl) URL.revokeObjectURL(pdfUrl);
        pdfUrl = null;
    }

    onMount(async () => {
        // Wire Monaco worker
        (self as any).MonacoEnvironment = {
            getWorker: () => new EditorWorker()
        };

        // Create editor
        editor = monaco.editor.create(editorEl, {
            value: '',
            language: 'plaintext',        // LaTeX -> plaintext is fine; switch to 'html' for .html
            fontSize: 14,
            automaticLayout: true,
            minimap: { enabled: false },
            // theme: 'vs-dark'           // uncomment if you prefer dark
        });

        // Load template from backend
        try {
            const tpl = await getPdfTemplate();
            editor.setValue(tpl ?? '');
        } catch (e) {
            editor.setValue('% Failed to load template.\n% ' + (e as Error)?.message);
        }
    });

    onDestroy(() => {
        revokePdfUrl();
        editor?.dispose();
    });

    // --- shared downloader for existing buttons ---
    async function downloadFile(
        generateFn: (zspId: string) => Promise<string | ArrayBuffer>,
        fileType: string,
        extension: string
    ) {
        try {
            isLoading = true;
            const content = await generateFn(zspId);
            const blob = new Blob([content], { type: fileType });
            const url = URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = `tm_${zspId}.${extension}`;
            a.style.display = 'none';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);

            URL.revokeObjectURL(url);
        } finally {
            isLoading = false;
        }
    }

    const downloadCsv = () => downloadFile(generateCsv, 'text/csv', 'csv');
    const downloadDetailedCsv = () => downloadFile(generateDetailedCsv, 'text/csv', 'csv');
    const downloadHtmlResults = () => downloadFile(generateHtmlResults, 'text/html', 'html');
    const downloadShortPdfResults = () => downloadFile(generateShortPdfResults, 'application/pdf', 'pdf');

    // --- NEW: Save template, then generate PDF and preview on the right ---
    async function saveAndPreviewPdf() {
        if (!zspId?.trim()) return;
        if (!editor) return;

        try {
            isGenerating = true;

            // 1) Save template first
            const current = editor.getValue();
            await savePdfTemplate(current);

            // 2) Now generate PDF
            const content = await generatePdfResults(zspId); // should return ArrayBuffer
            revokePdfUrl();
            const blob = new Blob([content], { type: 'application/pdf' });
            pdfUrl = URL.createObjectURL(blob);
        } finally {
            isGenerating = false;
        }
    }
</script>

<style>
    /* reasonable height for editor/preview area */
    .pane {
        height: 75vh;
        min-height: 420px;
    }
</style>

<div class="flex flex-col gap-5">
    <h1>Rankingowy Analizator Końcowy</h1>

    <!-- Top row with ZSP and downloads (unchanged) -->
    <div class="flex flex-col gap-3">
        <input bind:value={zspId} class="input" placeholder="ZSP ID" type="text" />
        <div class="flex gap-3 items-center flex-wrap">
            <button class="btn btn-md variant-filled-secondary h-10" disabled={isLoading} on:click={downloadCsv}>
                Generuj TM csv
            </button>
            <button class="btn btn-md variant-filled-secondary h-10" disabled={isLoading}
                    on:click={downloadDetailedCsv}>
                Generuj szczegółowy csv
            </button>
            <button class="btn btn-md variant-filled-secondary h-10" disabled={isLoading}
                    on:click={downloadHtmlResults}>
                Generuj html results
            </button>
            <!-- Keep the "short" download as before -->
            <button class="btn btn-md variant-filled-secondary h-10" disabled={isLoading}
                    on:click={downloadShortPdfResults}>
                Generuj skrócony pdf
            </button>
            {#if isLoading}
                <ProgressRadial width="w-6"/>
            {/if}
        </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div class="flex flex-col">
            <div class="text-sm opacity-70 mb-2">Szablon LaTeX: <code>results.latex</code></div>
            <div bind:this={editorEl} class="border rounded pane overflow-hidden"></div>

            <div class="flex gap-3 items-center mt-3">
                <button
                        class="btn btn-md variant-filled-secondary h-10"
                        disabled={isGenerating || !zspId}
                        on:click={saveAndPreviewPdf}
                        title={!zspId ? 'Podaj ZSP ID' : 'Zapisz szablon i wygeneruj PDF'}
                >
                    Zapisz szablon i generuj PDF (podgląd)
                </button>
                {#if isGenerating}
                    <ProgressRadial width="w-6"/>
                {/if}
            </div>
        </div>

        <div class="border rounded pane overflow-hidden">
            {#if pdfUrl}
                <iframe src={pdfUrl} class="w-full h-full" title="Podgląd PDF" />
            {:else}
                <div class="p-6 text-sm opacity-70">Wygeneruj PDF, aby zobaczyć podgląd tutaj.</div>
            {/if}
        </div>
    </div>
</div>
