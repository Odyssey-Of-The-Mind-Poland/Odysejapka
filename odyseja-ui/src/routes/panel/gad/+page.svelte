<script lang="ts">
    import {getGadStatus, runGad, stopGadRun} from "./gadService";
    import {type GadRequest, type Progress, type PunctationCells, Status} from "$lib/types.js";
    import {onDestroy, onMount} from "svelte";
    import {ProgressBar} from "@skeletonlabs/skeleton";

    export let data: GadRequest;
    let gadProgress: Progress = {status: Status.STOPPED, progress: 100};
    let intervalId: any = null;

    onMount(() => {
        intervalId = setInterval(() => {
            getGadStatus().then((progress) => {
                gadProgress = progress;
            });
        }, 5000);
    });

    onDestroy(() => {
        if (intervalId) {
            clearInterval(intervalId);
        }
    });

    function updateProblemPunctuationCells(index, field, value) {
        let cell = data.problemPunctuationCells[index] || {
            dt: '',
            style: '',
            penalty: '',
            balsa: '',
            anomaly: '',
        } as PunctationCells;
        cell[field] = value;
        data.problemPunctuationCells[index] = cell;
    }

    function startGad() {
        runGad(data);
        gadProgress = {status: Status.RUNNING, progress : 0};
    }

    function stopGad() {
        gadProgress = {status: Status.STOPPED, progress : 0};
        stopGadRun();
    }
</script>

<h1 class="m-5">GAD</h1>

<div>
        <label class="ml-5">Folder z bazowymi arkuszami <input
                bind:value={data.templatesFolderId}
                class="input m-5"
                placeholder="Folder z bazowymi arkuszami"
                type="text"
        /></label>


        <label class="ml-5">Folder dla wygenerowanych arkuszy <input
                bind:value={data.destinationFolderId}
                class="input m-5"
                placeholder="Folder dla wygenerowanych arkuszy"
                type="text"
        /></label>


        <label class="ml-5">link do ZSP <input
                bind:value={data.zspId}
                class="input m-5"
                placeholder="link do ZSP"
                type="text"
        /></label>


</div>

{#each [1, 2, 3, 4, 5] as i, _}
    <h3 class="ml-5">
        Problem {i}
    </h3>
    <div class="flex flex-wrap space-x-5 m-5">
        <label class="ml-5">
            DT:  <input class="input flex-grow flex-1" type="text"
                        value="{data.problemPunctuationCells[i]?.dt ?? ''}"
                        on:input={(e) => updateProblemPunctuationCells(i, 'dt', e.target.value)}
                        placeholder="DT"/>
        </label>
        <label class="ml-5">Styl: <input class="input flex-grow flex-1" type="text"
                            value="{data.problemPunctuationCells[i]?.style ?? ''}"
                            on:input={(e) => updateProblemPunctuationCells(i, 'style', e.target.value)}
                            placeholder="Komórka za styl"/></label>
        <label class="ml-5">
            Karne:
            <input class="input flex-grow flex-1" type="text"
                   value="{data.problemPunctuationCells[i]?.penalty ?? ''}"
                   on:input={(e) => updateProblemPunctuationCells(i, 'penalty', e.target.value)}
                   placeholder="Komórka Punktów karnych"/>
        </label>



        {#if i === 4}
            <label class="ml-5">
                Balsa waga:  <input class="input flex-grow flex-1" type="text"
                                    value="{data.problemPunctuationCells[i]?.balsa ?? ''}"
                                    on:input={(e) => updateProblemPunctuationCells(i, 'balsa', e.target.value)}
                                    placeholder="Balsa"/>
            </label>

        {/if}
        <label class="ml-5">
            Anomalia:  <input class="input flex-grow flex-1" type="text"
                              value="{data.problemPunctuationCells[i]?.anomaly ?? ''}"
                              on:input={(e) => updateProblemPunctuationCells(i, 'anomaly', e.target.value)}
                              placeholder="Komórka Anomalii"/>
        </label>
        <label class="ml-5">
            Czas występu:
            <input class="input flex-grow flex-1" type="text"
                   value="{data.problemPunctuationCells[i]?.actualPerformanceStartTime ?? ''}"
                   on:input={(e) => updateProblemPunctuationCells(i, 'actualPerformanceStartTime', e.target.value)}
                   placeholder="Komórka faktycznego czasu występu drużyny"/>
        </label>


    </div>
{/each}


<div class="m-5">
    {#if gadProgress.status === Status.STOPPED}
        <button class="btn btn-md variant-filled-secondary h-10" on:click={startGad}>Generuj arkusze</button>
    {:else}
        <button class="btn btn-md variant-filled-error h-10 mb-5" on:click={stopGad}>Zatrzymaj generowanie</button>
        <ProgressBar label="Progress Bar" value={gadProgress.progress} max={100}/>
    {/if}
</div>
