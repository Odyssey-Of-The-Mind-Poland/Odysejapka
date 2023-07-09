<script lang="ts">
  import type {InfoCategory, Infos} from '../types';
  import {Table, tableMapperValues} from "@skeletonlabs/skeleton";
  import type {TableSource} from '@skeletonlabs/skeleton';

  export let data: Infos;

  $: selectedCategory = data.categories[0] as InfoCategory;

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
    let info = data.infos.find(info => info.id === event.detail[0]);
  }
</script>

{#each data.categories as category}
  <div class="card card-hover cursor-pointer mb-6">
    <header class="card-header">{category.name}</header>
    <section class="p-4">
      <Table source={mapInfosToTable(category)} interactive="true" on:selected={onInfoSelected}/>
    </section>
  </div>
{/each}