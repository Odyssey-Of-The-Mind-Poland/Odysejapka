<script lang="ts">
  import {put} from '$lib/apiService';
  import type {Problems} from '$lib/types';

  export let data: Problems;

  let initialData = JSON.parse(JSON.stringify(data))

  $: isChanged = JSON.stringify(data) !== JSON.stringify(initialData);

  function save() {
    saveProblems(data.problems);
    initialData = JSON.parse(JSON.stringify(data));
    toggleEdit();
  }

  async function saveProblems(problems: Problems) {
    await put(problems, "/problem", 'Problemy zapisano pomyślnie')
  }


  let editToggled = false
  function toggleEdit() {
    editToggled = !editToggled;
    editToggled = editToggled;
  }

  function cancelChanges() {
    toggleEdit();
    data = JSON.parse(JSON.stringify(initialData));
  }

</script>

  <section class="p-4">
    <div class="flex justify-between items-center max-w-2xl">
      <h2 class="mb-6">Problemy</h2>
      <button type="button" on:click={toggleEdit} class="btn btn-md variant-filled-primary"
              disabled='{editToggled}'>Edytuj</button>
    </div>
    {#each data.problems as problem}
      <label class="label py-3 flex items-center max-w-2xl">
        <span class="text-2xl font-semibold pr-4">{problem.id}</span>
        <input class="input text-xl" type="text" placeholder="Input" bind:value={problem.name} disabled="{!editToggled}"/>
      </label>
    {/each}
  </section>

  <footer class="pl-8">
    <button type="button" on:click={save} disabled='{!isChanged}' class="btn btn-md variant-filled-secondary">
      Zapisz
    </button>
    <button type="button" on:click={cancelChanges} disabled='{!editToggled}' class="btn btn-md variant-ringed-surface">
      Anuluj
    </button>
  </footer>
