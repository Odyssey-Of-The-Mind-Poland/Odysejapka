<script lang="ts">
    import {BASE_URL, del, showSadToast} from "$lib/apiService";
    import type {City, Sponsors} from "$lib/types";
    import {fetchSponsors} from "./sponsorService";
    import {city} from "$lib/cityStore";

    export let data: Sponsors;

    let currentCity: City
    city.subscribe(async curr => {
        currentCity = curr
        data = await fetchSponsors();
    });

    let rowIndex = 0;

    async function handleImageUpload(event: Event, index: number) {
        const file = (event.target as HTMLInputElement).files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = async () => {
                if (typeof reader.result === "string") {
                    await uploadImageToServer(file, index, 0);
                }
            };
            reader.readAsDataURL(file);
            rowIndex++;
        }
    }

    async function uploadImageToServer(file: File, row: number, column: number) {
        const formData = new FormData();
        formData.append('image', file);
        formData.append('row', row.toString());
        formData.append('column', column.toString());
        formData.append('cityId', currentCity.id.toString());

        const response = await fetch(BASE_URL + '/sponsor', {
            method: 'POST',
            body: formData,
        });

        if (!response.ok) {
            showSadToast('Coś poszło nie tak :c')
            return;
        }

        data = await fetchSponsors();
    }

    async function deletePerformance(id: number) {
        await del('/sponsor/' + id, 'Sponsor usunięty')
        data = await fetchSponsors();
    }

</script>

<h2 class="mb-6">Sponsorzy</h2>
<label class="bg-green-500 text-white px-4 py-2 rounded cursor-pointer w-32 text-center">
    Add row
    <input type="file" class="hidden" accept="image/*"
            on:change={(e) => handleImageUpload(e, rowIndex)}/>
</label>

<main class="p-4">
    <table class="w-full mt-4">
        {#each data.sponsors as row, rowIndex}
            <tr>
                {#each row as sponsor}
                    <td class="border p-2">
                        <img src={BASE_URL + '/sponsor/' + sponsor.id} class="w-24"/>
                        <button
                                type="button"
                                class="btn btn-md variant-filled-error ml-4"
                                on:click={() => deletePerformance(sponsor.id)}>Usuń
                        </button>
                    </td>
                {/each}
                <td class="border p-2">
                    <label class="bg-green-500 text-white px-4 py-2 rounded cursor-pointer">
                        Add Image
                        <input type="file" class="hidden" accept="image/*"
                               on:change={(e) => handleImageUpload(e, rowIndex)}/>
                    </label>
                </td>
            </tr>
        {/each}
    </table>
</main>
