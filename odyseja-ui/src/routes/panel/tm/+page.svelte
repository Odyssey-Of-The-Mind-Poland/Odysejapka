<script>
    import {generateCsv, generateHtmlResults} from './tm';


    let zspId = "";

    async function downloadCsv() {
        let csv = await generateCsv(zspId);
        const blob = new Blob([csv], {type: 'text/csv'});
        const url = URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = `tm_${zspId}.csv`;
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        URL.revokeObjectURL(url);
    }

    async function downloadHtmlResults() {
        let csv = await generateHtmlResults(zspId);
        const blob = new Blob([csv], {type: 'text/html'});
        const url = URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = `tm_${zspId}.html`;
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        URL.revokeObjectURL(url);
    }

</script>

<h1 class="m-5">TM</h1>

<div class="flex flex-col gap-3">
    <input bind:value={zspId} class="input" placeholder="ZSP ID"
           type="text"/>
    <div class="flex gap-3">
        <button class="btn btn-md variant-filled-secondary h-10" on:click={downloadCsv}>Generuj csv</button>
        <button class="btn btn-md variant-filled-secondary h-10" on:click={downloadHtmlResults}>Generuj html results</button>
    </div>
</div>

