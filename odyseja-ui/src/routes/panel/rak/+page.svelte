<script lang="ts">
    import {
        generateCsv,
        generateDetailedCsv,
        generateHtmlResults,
        generatePdfResults,
        generateShortPdfResults
    } from './rak';
    import {ProgressRadial} from '@skeletonlabs/skeleton';

    let zspId = "";
    let isLoading = false;

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
    const downloadPdfResults = () => downloadFile(generatePdfResults, 'application/pdf', 'pdf');
    const downloadShortPdfResults = () => downloadFile(generateShortPdfResults, 'application/pdf', 'pdf');
</script>

<div class="flex flex-col gap-5">
    <h1>Rankingowy Analizator Końcowy</h1>

    <div class="flex flex-col gap-3">
        <input bind:value={zspId} class="input" placeholder="ZSP ID"
               type="text"/>
        <div class="flex gap-3 items-center">
            <button class="btn btn-md variant-filled-secondary h-10" on:click={downloadCsv} disabled={isLoading}>
                Generuj TM csv
            </button>
            <button class="btn btn-md variant-filled-secondary h-10" disabled={isLoading}
                    on:click={downloadDetailedCsv}>
                Generuj szczegółowy csv
            </button>
            <button class="btn btn-md variant-filled-secondary h-10" on:click={downloadHtmlResults} disabled={isLoading}>
                Generuj html results
            </button>
            <button class="btn btn-md variant-filled-secondary h-10" on:click={downloadPdfResults} disabled={isLoading}>
                Generuj pdf results
            </button>
            <button class="btn btn-md variant-filled-secondary h-10" on:click={downloadShortPdfResults} disabled={isLoading}>
                Generuj skrócony pdf
            </button>
            {#if isLoading}
                <ProgressRadial width="w-6" />
            {/if}
        </div>
    </div>
</div>
