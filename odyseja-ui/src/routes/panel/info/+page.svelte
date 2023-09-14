<script lang="ts">
    import type {Info, InfoCategory, Infos} from '$lib/types';
    import {Table, tableMapperValues} from "@skeletonlabs/skeleton";
    import type {TableSource} from '@skeletonlabs/skeleton';
    import {goto} from "$app/navigation";

    export let data: Infos;

    $: selectedCategory = data.categories[0] as InfoCategory;

    let infoDialog: HTMLDialogElement;
    let selectedInfo = data.infos[0] as Info;

    function mapInfosToTable(category): TableSource {
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
        </section>
    </div>
{/each}
