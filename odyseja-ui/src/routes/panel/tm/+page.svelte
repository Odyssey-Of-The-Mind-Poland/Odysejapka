<script>
    import {generateCsv} from './tm';


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

</script>

<h1 class="m-5">TM</h1>

<div>

    <input bind:value={zspId} class="input m-5" placeholder="ZSP ID"
           type="text"/>
    <button class="btn btn-md variant-filled-secondary h-10" on:click={downloadCsv}>Generuj csv</button>
</div>

