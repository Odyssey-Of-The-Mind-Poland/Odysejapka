<script lang="ts">
  import { saveProblems } from '../apiService';
  import type { Problems } from '../types';

  export let data: Problems;

  let initialData = JSON.parse(JSON.stringify(data))

  $: isChanged = JSON.stringify(data) !== JSON.stringify(initialData);

  function save() {
    saveProblems(data);
    initialData = JSON.parse(JSON.stringify(data));
  }
</script>

  <header class="h-40 bg-neutral-100 flex items-center px-12">
    <h2 class="text-4xl text-dark-500 font-medium">Problemy</h2>
  </header>

  <section class="p-4">
    {#each data.problems as problem}
      <label class="label m-4">
        <span>Problem nr. {problem.id}</span>
        <input class="input" type="text" placeholder="Input" bind:value={problem.name}/>
      </label>
    {/each}
  </section>

  <footer class="">
    <button type="button" on:click={save} disabled='{!isChanged}' class="btn btn-md variant-filled-primary">
      Zapisz
    </button>
  </footer>
