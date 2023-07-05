<script lang="ts">
  import { saveProblems } from '../apiService';
  import type { Problems } from '../types';
  import BasicButton from "$lib/Buttons/BasicButton.svelte";

  let isEditable = false, showModal = false;

  function addProblem() {
    showModal = !showModal;
  }

  function toggleEdit() {
    isEditable = !isEditable;
  }

  function saveClicked() {
    toggleEdit();
    save();
  }

  function cancelClicked() {
    toggleEdit();
    data = JSON.parse(JSON.stringify(initialData));
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
<!--      <BasicButton text="Dodaj" icon="add" on:click={addProblem}/>-->
      <BasicButton text="Edytuj" icon="edit" on:click={toggleEdit} disabled="{isEditable}"/>
    </div>
  </header>

  <section class="-mt-8 relative w-11/12 mx-auto outline outline-1 outline-neutral-200 rounded bg-white px-6 py-6">
    {#each data.problems as problem}
      <div class="first:mt-0 mt-4">
        <span class="text-sm font-normal text-dark-200 ml-2">Numer {problem.id}</span>
          <div class="w-full h-full flex items-center rounded outline outline-1 outline-neutral-200 focus:outline-blue-500">
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

  {#if isEditable}
    <footer class="w-full py-4 px-12 flex justify-center gap-4">
      <BasicButton text="Zapisz" icon="check" on:click={saveClicked} disabled='{!isChanged}'/>
      <BasicButton text="Anuluj" icon="close" on:click={cancelClicked}/>
    </footer>
    {#if isChanged}
      <span class="text-blue-500">Są zmiany do zapisania</span>
    {:else}
      <span class="text-dark-100">Brak zmian</span>
    {/if}
  {/if}
