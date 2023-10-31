<script lang="ts">

    import type {BreakingChange} from "$lib/types";
    import {put} from "$lib/apiService";
    import {fetchBreakingChange} from "./breakingChangeService";

    export let data: BreakingChange

    async function save() {
        await put(data, '/breaking/change', 'Zmiany zostały zapisane')
        await fetchBreakingChange()
    }
</script>

<div class="flex">
    <label class="label p-2">
        <input bind:value={data.version} class="input" placeholder="Input" type="text"/>
    </label>
</div>

<button class="btn btn-md variant-filled-secondary h-10" on:click={save} type="button">Zapisz</button>