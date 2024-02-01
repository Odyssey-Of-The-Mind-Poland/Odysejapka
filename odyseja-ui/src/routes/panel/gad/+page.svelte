<script lang="ts">
    import {runGad} from "./gadService";
    import type {GadRequest, PunctationCells} from "$lib/types.js";

    export let data: GadRequest;

    function updateProblemPunctuationCells(index, field, value) {
        let cell = data.problemPunctuationCells[index] || {dt: '', style: '', penalty: ''} as PunctationCells;
        cell[field] = value;
        data.problemPunctuationCells[index] = cell;
    }

    function startGad() {
        runGad(data);
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

<button class="btn btn-md variant-filled-secondary h-10 m-5" on:click={startGad}>Generuj arkusze</button>
