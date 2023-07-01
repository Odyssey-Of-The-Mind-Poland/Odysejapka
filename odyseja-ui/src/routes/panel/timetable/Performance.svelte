<script lang="ts">
  import { deletePerformance, savePerformance } from '../apiService';
  import type { Performance } from '../types';

  export let performance: Performance;
  export let onSave;

  async function save() {
    await savePerformance(performance);
    onSave();
  }


  async function deletePerf() {
    await deletePerformance(performance.id);
    onSave();
  }

</script>

<form class="space-y-1.5">
    <label class="label">
        <span>Drużyna</span>
        <input class="input" type="text" bind:value={performance.team}/>
    </label>

    <div class="flex flex-wrap space-x-5">
        <label class="label flex-grow">
            <span>Problem</span>
            <select class="select" bind:value={performance.problem}>
                <option value={0}>Juniorki</option>
                <option value={1}>1</option>
                <option value={2}>2</option>
                <option value={3}>3</option>
                <option value={4}>4</option>
                <option value={5}>5</option>
            </select>
        </label>

        <label class="label flex-grow">
            <span>Grupa wiekowa</span>
            <select class="select" type="number" bind:value={performance.age}>
                <option value={0}>Juniorki</option>
                <option value={1}>1</option>
                <option value={2}>2</option>
                <option value={3}>3</option>
                <option value={4}>4</option>
                <option value={5}>5</option>
            </select>
        </label>

        <label class="label flex-grow">
            <span>Scena</span>
            <select class="select" type="number" bind:value={performance.stage}>
                <option value={1}>1</option>
                <option value={2}>2</option>
                <option value={3}>3</option>
                <option value={4}>4</option>
                <option value={5}>5</option>
                <option value={6}>6</option>
                <option value={7}>7</option>
                <option value={8}>8</option>
                <option value={9}>9</option>
            </select>
        </label>
    </div>

    <div class="flex flex-wrap space-x-5">
        <label class="label flex-grow flex-1">
            <span>Dzień występu</span>
            <select class="select" bind:value={performance.performanceDay}>
                <option value="Sobota">Sobota</option>
                <option value="Niedziela">Niedziela</option>
            </select>
        </label>

        <label class="label flex-grow flex-1">
            <span>Godzina występu</span>
            <input class="input" type="text" bind:value={performance.performance}/>
        </label>
    </div>

    <div class="flex flex-wrap space-x-5">
        <label class="label flex-grow flex-1">
            <span>Dzień spontanu</span>
            <select class="select" bind:value={performance.spontanDay}>
                <option value="Sobota">Sobota</option>
                <option value="Niedziela">Niedziela</option>
            </select>
        </label>


        <label class="label flex-grow flex-1">
            <span>Godzina spontanu</span>
            <input class="input" type="text" bind:value={performance.spontan}/>
        </label>
    </div>

    <div class="flex flex-wrap space-x-5">
        <label class="label flex-grow flex-1">
            <span>Część</span>
            <select class="select" bind:value={performance.part}>
                <option value={0}>0</option>
                <option value={1}>1</option>
                <option value={2}>2</option>
            </select>
        </label>

        <label class="label flex-grow flex-1">
            <span>Liga</span>
            <select class="select" bind:value={performance.league}>
                <option value=""></option>
                <option value="A">A</option>
                <option value="B">B</option>
                <option value="C">C</option>
            </select>
        </label>
    </div>

  <button
          type="button"
          class="btn btn-md variant-filled-primary"
          on:click={save}>Zapisz
  </button>

  {#if (performance.id != -1)}
    <button
            type="button"
            class="btn btn-md variant-filled-error ml-4"
            on:click={deletePerf}>Usuń
    </button>
  {/if}
</form>
