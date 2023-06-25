<script lang="ts">
    import type {Performance} from '../types';
    import {savePerformance} from '../apiService';
    import cloneDeep from "lodash/cloneDeep";

    export let performance: Performance;
    export let onSave;

    let initialData = cloneDeep(performance);

    $: isChanged = JSON.stringify(performance) !== JSON.stringify(initialData);

    let selectedProblem = performance.problem;
    $: performance.problem = selectedProblem;

    let selectedAge = performance.age;
    $: performance.age = selectedAge;

    let selectedStage = performance.stage;
    $: performance.stage = selectedStage;

    let selectedPerformanceDay = performance.performanceDay;
    $: performance.spontanDay = selectedPerformanceDay;


    let selectedSpontanDay = performance.spontanDay;
    $: performance.spontanDay = selectedSpontanDay;

    let selectedSpontan = performance.spontan;
    $: performance.spontan = selectedSpontan;

    let selectedPart = performance.part;
    $: performance.part = selectedPart;

    let selectedLeague = performance.league;
    $: performance.league = selectedLeague;

    async function save() {
        await savePerformance(performance);
        initialData = cloneDeep(performance);
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
            <select class="select" bind:value={selectedProblem}>
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
            <select class="select" type="number" bind:value={selectedAge}>
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
            <select class="select" type="number" bind:value={selectedStage}>
                <option value={1}>1</option>
                <option value={2}>2</option>
                <option value={3}>3</option>
                <option value={4}>4</option>
                <option value={5}>5</option>
            </select>
        </label>
    </div>

    <div class="flex flex-wrap space-x-5">
        <label class="label flex-grow">
            <span>Dzień występu</span>
            <input class="input" type="text" bind:value={performance.performanceDay}/>
        </label>

        <label class="label flex-grow">
            <span>Godzina występu</span>
            <select class="select" bind:value={selectedPerformanceDay}>
                <option value="Sobota">Sobota</option>
                <option value="Niedziela">Niedziela</option>
            </select>
        </label>
    </div>

    <div class="flex flex-wrap space-x-5">
        <label class="label flex-grow">
            <span>Dzień spontanu</span>
            <select class="select" bind:value={selectedSpontanDay}>
                <option value="Sobota">Sobota</option>
                <option value="Niedziela">Niedziela</option>
            </select>
        </label>


        <label class="label flex-grow">
            <span>Godzina spontanu</span>
            <input class="input" type="text" bind:value={performance.spontan}/>
        </label>
    </div>

    <div class="flex flex-wrap space-x-5">
        <label class="label flex-grow">
            <span>Część</span>
            <select class="select" bind:value={selectedPart}>
                <option value={0}>0</option>
                <option value={1}>1</option>
                <option value={2}>2</option>
            </select>
        </label>

        <label class="label flex-grow">
            <span>Liga</span>
            <select class="select" bind:value={selectedLeague}>
                <option value={0}>0</option>
                <option value={1}>1</option>
                <option value={2}>2</option>
            </select>
        </label>
    </div>

    <button
            type="button"
            class="btn btn-md variant-filled-primary"
            on:click={save}
            disabled='{!isChanged}'>Zapisz
    </button>
</form>
