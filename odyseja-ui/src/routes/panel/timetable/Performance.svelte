<script lang="ts">
    import type { Performance } from '../types';
    import { savePerformance } from '../apiService';
    import cloneDeep from "lodash/cloneDeep";

    export let performance: Performance;
    export let onSave;

    let initialData = cloneDeep(performance);

    $: isChanged = JSON.stringify(performance) !== JSON.stringify(initialData);

    async function save() {
        await savePerformance(performance);
        initialData = cloneDeep(performance);
        onSave();
    }
</script>

<form class="space-y-1.5">
    <label class="label">
        <span>Drużyna</span>
        <input class="input" type="text" bind:value={performance.team} />
    </label>

    <label class="label">
        <span>Problem</span>
        <input class="input" type="number" bind:value={performance.problem} />
    </label>

    <label class="label">
        <span>Grupa wiekowa</span>
        <input class="input" type="number" bind:value={performance.age} />
    </label>

    <label class="label">
        <span>Scena</span>
        <input class="input" type="number" bind:value={performance.stage} />
    </label>

    <label class="label">
        <span>Godzina występu</span>
        <input class="input" type="text" bind:value={performance.performance} />
    </label>

    <label class="label">
        <span>Godzina spontanu</span>
        <input class="input" type="text" bind:value={performance.spontan} />
    </label>

    <label class="label">
        <span>Część</span>
        <input class="input" type="number" bind:value={performance.part} />
    </label>

    <label class="label">
        <span>Dzień występu</span>
        <input class="input" type="text" bind:value={performance.performanceDay} />
    </label>

    <label class="label">
        <span>Dzień spontanu</span>
        <input class="input" type="text" bind:value={performance.spontanDay} />
    </label>

    <label class="label">
        <span>Liga</span>
        <input class="input" type="text" bind:value={performance.league} />
    </label>

    <button
            type="button"
            class="btn btn-md variant-filled-primary"
            on:click={save}
            disabled='{!isChanged}'>Zapisz
    </button>
</form>
