<script>
    import {generateCsv} from './tm.ts';


    let zspId = "";

</script>

<h1 class="m-5">TM</h1>

<div>

    <input bind:value={zspId} class="input m-5" placeholder="ZSP ID"
           type="text"/>
    <button class="btn btn-md variant-filled-secondary h-10" on:click={generateCsv}>Generuj csv</button>
</div>

