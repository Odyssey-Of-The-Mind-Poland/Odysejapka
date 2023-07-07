<script lang="ts">
  import { saveProblems } from '../apiService';
  import type { Problems } from '../types';
  import BasicButton from "$lib/BasicButton/BasicButton.svelte";

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
      <BasicButton text="Dodaj" icon="add"/>
      <BasicButton text="Edytuj" icon="edit" />
    </div>
  </header>

  <section class="-mt-8 relative w-11/12 mx-auto outline outline-1 outline-neutral-200 rounded bg-white px-6 py-6">
    {#each data.problems as problem}
      <div class="first:mt-0 mt-4">
        <span class="text-sm font-normal text-dark-200 ml-2">Numer {problem.id}</span>
        <div class="rounded outline outline-1 outline-neutral-200 px-4 py-2">
          <span class="text-xl text-dark-500">{problem.name}</span>
        </div>
      </div>
    {/each}
  </section>

<!--  <footer class="">-->
<!--    <button type="button" on:click={save} disabled='{!isChanged}' class="btn btn-md variant-filled-primary">-->
<!--      Zapisz-->
<!--    </button>-->
<!--  </footer>-->
