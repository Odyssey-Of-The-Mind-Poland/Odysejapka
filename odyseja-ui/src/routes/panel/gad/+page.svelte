<script lang="ts">
    import {getGadStatus, runGad, stopGadRun} from "./gadService";
    import {type GadRequest, GadStatus, type PunctationCells} from "$lib/types.js";
    import {onDestroy, onMount} from "svelte";

    export let data: GadRequest;
    let gadStatus: GadStatus = GadStatus.STOPPED;
    let intervalId: any = null;

    onMount(() => {
        intervalId = setInterval(() => {
            getGadStatus().then((status) => {
                gadStatus = status;
            });
        }, 5000);
    });

    onDestroy(() => {
        if (intervalId) {
            clearInterval(intervalId);
        }
    });

    function updateProblemPunctuationCells(index, field, value) {
        let cell = data.problemPunctuationCells[index] || {dt: '', style: '', penalty: ''} as PunctationCells;
        cell[field] = value;
        data.problemPunctuationCells[index] = cell;
    }

    function startGad() {
        runGad(data);
        gadStatus = GadStatus.RUNNING;
    }

    function stopGad() {
        gadStatus = GadStatus.STOPPED;
        stopGadRun();
    }
</script>

<h1 class="m-5">GAD</h1>

<div>
    <input bind:value={data.templatesFolderId} class="input m-5" placeholder="Folder z bazowymi arkuszami" type="text"/>
    <input bind:value={data.destinationFolderId} class="input m-5" placeholder="Folder dla wygenerowanych arkuszy"
           type="text"/>
    <input bind:value={data.zspId} class="input m-5" placeholder="link do ZSP" type="text"/>
</div>

{#each [1, 2, 3, 4, 5] as i, _}
    <h3 class="ml-5">
        Problem {i}
    </h3>
    <div class="flex flex-wrap space-x-5 m-5">
        <input class="input flex-grow flex-1" type="text"
               value="{data.problemPunctuationCells[i]?.dt ?? ''}"
               on:input={(e) => updateProblemPunctuationCells(i, 'dt', e.target.value)}
               placeholder="DT"/>
        <input class="input flex-grow flex-1" type="text"
               value="{data.problemPunctuationCells[i]?.style ?? ''}"
               on:input={(e) => updateProblemPunctuationCells(i, 'style', e.target.value)}
               placeholder="Komórka za styl"/>
        <input class="input flex-grow flex-1" type="text"
               value="{data.problemPunctuationCells[i]?.penalty ?? ''}"
               on:input={(e) => updateProblemPunctuationCells(i, 'penalty', e.target.value)}
               placeholder="Komórka Punktów karnych"/>
    </div>
{/each}


{#if gadStatus === GadStatus.STOPPED}
    <button class="btn btn-md variant-filled-secondary h-10 m-5" on:click={startGad}>Generuj arkusze</button>
{:else}
    <button class="btn btn-md variant-filled-error h-10 m-5" on:click={stopGad}>Zatrzymaj generowanie</button>
{/if}
