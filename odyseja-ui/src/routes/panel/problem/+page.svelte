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

<div class="card card-hover cursor-pointer mb-6">
  <header class="card-header"><h2>Problemy</h2></header>
  <section class="p-4">
    {#each data.problems as problem}
      <label class="label m-4">
        <span>Problem nr. {problem.id}</span>
        <input class="input" type="text" placeholder="Input" bind:value={problem.name}/>
      </label>
    {/each}
  </section>
  <footer class="card-footer">
    <button
            type="button"
            class="btn btn-md variant-filled-primary"
            on:click={save}
            disabled='{!isChanged}'>Zapisz
    </button>
  </footer>
</div>
