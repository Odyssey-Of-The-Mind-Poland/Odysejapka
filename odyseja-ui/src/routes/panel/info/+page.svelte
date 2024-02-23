<script lang="ts">
    import type {City, Info, InfoCategory, Infos} from '$lib/types';
    import type {TableSource} from '@skeletonlabs/skeleton';
    import {Table, tableMapperValues} from "@skeletonlabs/skeleton";
    import {goto} from "$app/navigation";
    import {city} from "$lib/cityStore";
    import {fetchInfo, saveInfo} from "./infoService.js";

    export let data: Infos;

    $: selectedCategory = data.categories[0] as InfoCategory;

    let infoDialog: HTMLDialogElement;
    let selectedInfo = data.infos[0] as Info;
    let currentCity: City

    city.subscribe(async current => {
        data = await fetchInfo();
        currentCity = current;
    });

    async function addInfo(category: InfoCategory) {
        let sortNumber = data.infos.filter(info => info.categoryName === category.name)
            .reduce((prev, current) => (prev.sortNumber > current.sortNumber) ? prev : current, {sortNumber: -Infinity})
            .sortNumber;
        let info = await saveInfo({
            id: 0,
            infoName: '',
            infoText: '',
            city: currentCity.id,
            category: category.id,
            sortNumber: sortNumber + 1,
            categoryName: category.name
        } as Info);

        await goto(`info/edit/${info.id}`);
    }

    function mapInfosToTable(category: InfoCategory): TableSource {
        let infos = data.infos.filter(info => info.categoryName === category.name);
        return {
            head: ['Nazwa'],
            body: tableMapperValues(infos, ['infoName']),
            meta: tableMapperValues(infos, ['id', 'infoName']),
            foot: [`<code class="code">${infos.length}</code>`]
        };
    }

    function onInfoSelected(event) {
        selectedInfo = data.infos.find(info => info.id === event.detail[0]) as Info;
        goto(`info/edit/${selectedInfo.id}`)
    }

    function onInfoSaved(event) {
        infoDialog.close();
    }
</script>


<h1>Informacje</h1>

{#each data.categories as category}
    <div class="card card-hover cursor-pointer mb-6">
        <header class="card-header">{category.name}</header>
        <section class="p-4">
            <Table source={mapInfosToTable(category)} interactive="true" on:selected={onInfoSelected}/>
            <button
                    type="button"
                    class="btn variant-filled-primary h-full ml-5"
                    on:click={addInfo(category)}>Dodaj
            </button>
        </section>
    </div>
{/each}
