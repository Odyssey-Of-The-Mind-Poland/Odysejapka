<script lang="ts">
    import {onMount, onDestroy} from 'svelte';
    import {
        generateCsv,
        generateDetailedCsv,
        generateHtmlResults,
        generatePdfResults,
        generateShortPdfResults,
        getPdfTemplate,
        savePdfTemplate
    } from './rak';
    import {ProgressRadial} from '@skeletonlabs/skeleton';

    import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
    import EditorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker';

    let zspId = '';
    let isLoading = false;
    let isGenerating = false;

    let editorEl: HTMLDivElement;
    let editor: monaco.editor.IStandaloneCodeEditor | null = null;
    let pdfUrl: string | null = null;

    function revokePdfUrl() {
        if (pdfUrl) URL.revokeObjectURL(pdfUrl);
        pdfUrl = null;
    }

    onMount(async () => {
        (self as any).MonacoEnvironment = {
            getWorker: () => new EditorWorker()
        };

        editor = monaco.editor.create(editorEl, {
            value: '',
            language: 'plaintext',
            fontSize: 14,
            automaticLayout: true,
            minimap: {enabled: false},
        });

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

    async function downloadFile(
        generateFn: (zspId: string) => Promise<string | ArrayBuffer>,
        fileType: string,
        extension: string
    ) {
        try {
            isLoading = true;
            const content = await generateFn(zspId);
            const blob = new Blob([content], {type: fileType});
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

    async function saveAndPreviewPdf() {
        if (!zspId?.trim()) return;
        if (!editor) return;

        try {
            isGenerating = true;
            const current = editor.getValue();
            await savePdfTemplate(current);
            const content = await generatePdfResults(zspId);
            revokePdfUrl();
            const blob = new Blob([content], {type: 'application/pdf'});
            pdfUrl = URL.createObjectURL(blob);
        } finally {
            isGenerating = false;
        }
    }

    async function saveAndPreviewShortPdf() {
        if (!zspId?.trim()) return;
        if (!editor) return;

        try {
            isGenerating = true;
            const current = editor.getValue();
            await savePdfTemplate(current);
            const content = await generateShortPdfResults(zspId);
            revokePdfUrl();
            const blob = new Blob([content], {type: 'application/pdf'});
            pdfUrl = URL.createObjectURL(blob);
        } finally {
            isGenerating = false;
        }
    }
</script>

<style>
    .pane {
        height: 75vh;
        min-height: 420px;
    }
</style>

<div class="flex flex-col gap-5">
    <h1>Rankingowy Analizator Końcowy</h1>

    <div class="flex flex-col gap-3">
        <input bind:value={zspId} class="input" placeholder="ZSP ID" type="text"/>

        <div class="flex gap-3 items-center mt-3">
            <button
                    class="btn btn-md variant-filled-secondary h-10"
                    on:click={saveAndPreviewPdf}
                    disabled={isGenerating || !zspId}
                    title={!zspId ? 'Podaj ZSP ID' : 'Zapisz szablon i wygeneruj PDF'}
            >
                Pełne wyniki
            </button>
            <button
                    class="btn btn-md variant-filled-secondary h-10"
                    on:click={saveAndPreviewShortPdf}
                    disabled={isGenerating || !zspId}
                    title={!zspId ? 'Podaj ZSP ID' : 'Zapisz szablon i wygeneruj PDF'}
            >
                Skrócone wyniki
            </button>
            {#if isGenerating}
                <ProgressRadial width="w-6"/>
            {/if}
        </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div class="flex flex-col">
            <div class="border rounded pane overflow-hidden" bind:this={editorEl}></div>
        </div>

        <div class="border rounded pane overflow-hidden">
            {#if pdfUrl}
                <iframe src={pdfUrl} class="w-full h-full" title="Podgląd PDF"/>
            {:else}
                <div class="p-6 text-sm opacity-70">Wygeneruj PDF, aby zobaczyć podgląd tutaj.</div>
            {/if}
        </div>
    </div>
</div>
