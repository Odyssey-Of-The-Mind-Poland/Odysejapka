<script lang="ts">
  import type { Problems } from '../types';

  export let data: Problems;
  console.log('data', data);
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
</div>