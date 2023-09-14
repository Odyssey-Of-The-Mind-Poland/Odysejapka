<script lang="ts">
    import type {PerformanceGroup} from '$lib/types';
    import {deletePerformance, savePerformance} from "./performanceService";

    export let performanceGroup: PerformanceGroup;
    export let onSave;

    async function save() {
        for (let performance of performanceGroup.performances) {
            performance.city = performanceGroup.group.city;
            performance.problem = performanceGroup.group.problem;
            performance.age = performanceGroup.group.age;
            performance.stage = performanceGroup.group.stage;
            performance.part = performanceGroup.group.part;
            performance.league = performanceGroup.group.league;
            await savePerformance(performance);
        }
        onSave();
    }

    async function deletePerf() {
      for (let performance of performanceGroup.performances) {
        await deletePerformance(performance.id);
      }
      onSave();
    }
</script>

<form class="space-y-1.5">
    <div class="flex flex-wrap space-x-5">
        <label class="label flex-grow">
            <span>Problem</span>
            <select class="select" bind:value={performanceGroup.group.problem}>
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
            <select class="select" type="number" bind:value={performanceGroup.group.age}>
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
            <select class="select" type="number" bind:value={performanceGroup.group.stage}>
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
        <label class="label flex-grow">
            <span>Część</span>
            <select class="select" type="number" bind:value={performanceGroup.group.part}>
                <option value={0}>0</option>
                <option value={1}>1</option>
                <option value={2}>2</option>
            </select>
        </label>

        <label class="label flex-grow">
            <span>Liga</span>
            <select class="select" type="text" bind:value={performanceGroup.group.league}>
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

  <button
          type="button"
          class="btn btn-md variant-filled-error ml-4"
          on:click={deletePerf}>Usuń
  </button>
</form>
