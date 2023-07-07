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

  <section class="p-4">
    {#each data.problems as problem}
      <label class="label m-4">
        <span>Problem nr. {problem.id}</span>
        <input class="input" type="text" placeholder="Input" bind:value={problem.name}/>
      </label>
    {/each}
  </section>

  <footer class="pl-8">
    <button type="button" on:click={save} disabled='{!isChanged}' class="btn btn-md variant-filled-primary">
      Zapisz
    </button>
  </footer>
