<script lang="ts">
  import { saveProblems } from '../apiService';
  import type { Problems } from '../types';
  import BasicButton from "$lib/BasicButton/BasicButton.svelte";

  let isEditable = true

  function toggleEdit() {
    isEditable = !isEditable;
    isEditable = isEditable;
    console.log('edit toggled!')
  }

  export let data: Problems;

  let initialData = JSON.parse(JSON.stringify(data))

  $: isChanged = JSON.stringify(data) !== JSON.stringify(initialData);

  function save() {
    saveProblems(data);
    initialData = JSON.parse(JSON.stringify(data));
  }

</script>

  <header class="h-40 bg-neutral-100 flex items-center justify-between px-12 w-full">
    <h2 class="text-4xl text-dark-500 font-medium">Problemy</h2>
    <div class="flex gap-4">
      {#if isEditable}
        edit mode ON
      {/if}
      <BasicButton text="Dodaj" icon="add"/>
      <BasicButton text="Edytuj" icon="edit" on:click={toggleEdit} />
    </div>
  </header>

  <section class="-mt-8 relative w-11/12 mx-auto outline outline-1 outline-neutral-200 rounded bg-white px-6 py-6">
    {#each data.problems as problem}
      <div class="first:mt-0 mt-4">
        <span class="text-sm font-normal text-dark-200 ml-2">Numer {problem.id}</span>
          <div class="w-full h-full flex items-center rounded outline outline-1 outline-neutral-200">
            {#if isEditable}
              <span class="material-symbols-rounded text-dark-200 px-2">drag_indicator</span>
              <input type="text" bind:value={problem.name} class="border-0 focus:ring-0 h-full w-full
              px-0 py-2 text-xl text-dark-300 focus:text-dark-500">
            {:else}
              <input disabled type="text" bind:value={problem.name} class="border-0 h-full w-full text-xl text-dark-500">
            {/if}
          </div>
      </div>
    {/each}
  </section>

<!--  <footer class="">-->
<!--    <button type="button" on:click={save} disabled='{!isChanged}' class="btn btn-md variant-filled-primary">-->
<!--      Zapisz-->
<!--    </button>-->
<!--  </footer>-->
