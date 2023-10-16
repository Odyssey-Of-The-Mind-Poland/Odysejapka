<script lang="ts">
    import {post} from "$lib/apiService";
    import type {City} from "$lib/types";

    export let onSave;

    let cityName = '';

    async function save() {
        await post({id: 0, name: cityName} as City, '/city', 'Zapisano')
        onSave();
    }

</script>

<label class="label">
    <span>Drużyna</span>
    <input bind:value={cityName} class="input" type="text"/>
</label>

<button
        class="btn btn-md variant-filled-primary"
        on:click={save}
        type="button">Zapisz
</button>
