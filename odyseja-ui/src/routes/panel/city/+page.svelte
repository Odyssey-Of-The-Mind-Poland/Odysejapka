<script lang="ts">
    import type {Cities, City} from "$lib/types";
    import {del} from "$lib/apiService";
    import {fetchCities} from "./cityService";
    import Dialog from "$lib/Dialog.svelte";
    import CityDialog from "./CityDialog.svelte";

    export let data: Cities
    let cityDialog: HTMLDialogElement;

    async function addNew() {
        cityDialog.show()
    }

    async function deleteCity(city: City) {
        await del(`/city/${city.id}`, 'Miasto usunięte')
        await refresh()
    }

    async function refresh() {
        data = await fetchCities()
        cityDialog.close()
    }

</script>

<h2 class="mb-6">Miasta</h2>

<section class="p-4">
    {#each data.cities as city}
        <div class="flex">
            <label class="label p-2">
                <input class="input" type="text" placeholder="Input" bind:value={city.name} readonly/>
            </label>
            <button class="btn btn-md variant-filled-error h-10" type="button" on:click={deleteCity(city)}>Del</button>
        </div>
    {/each}
    <button class="btn btn-md variant-filled-secondary h-10" on:click={addNew} type="button">Dodaj</button>
</section>

<Dialog bind:dialog={cityDialog}>
    <CityDialog onSave={refresh}/>
</Dialog>
