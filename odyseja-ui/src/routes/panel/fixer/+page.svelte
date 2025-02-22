<script lang="ts">
    import {type FixSheetCommand, type ReplacementCell, runFixer, stopFixer} from "./fixerService";

    let pattern = ""
    let folderId = ""
    let running = false;
    let cells: ReplacementCell[] = [{sheetName: '', cell: '', value: ''}];

    function addCell() {
        cells = [...cells, { sheetName: '', cell: '', value: '' }];
    }

    function removeCell(index: number) {
        cells.splice(index, 1);
    }

    function fix() {
        runFixer({folderId: folderId, pattern: pattern, cells: cells} as FixSheetCommand)
        running = true;
    }

    function stopFixing() {
        stopFixer();
        running = false;
    }
</script>

<h1 class="m-5">Fixer</h1>

<div class="flex flex-col space-y-2 mb-2">
    <input bind:value={pattern} class="input flex-grow flex-1"
           placeholder="Arkusz pattern"
           type="text"/>

    <input bind:value={folderId} class="input flex-grow flex-1"
           placeholder="Folder z Arkuszami"
           type="text"/>

    {#each cells as cell, i}
        <div class="flex space-x-2">
            <input class="input flex-grow" type="text" bind:value={cell.sheetName} placeholder="Nazwa arkusza"/>
            <input class="input flex-grow" type="text" bind:value={cell.cell} placeholder="Komórka"/>
            <input class="input flex-grow" type="text" bind:value={cell.value} placeholder="Podmieniona wartość"/>
            <button class="btn btn-md variant-filled-error h-10" on:click={() => removeCell(i)}>Remove</button>
        </div>
    {/each}

    <button class="btn btn-md variant-filled-secondary h-10" on:click={addCell}>Add Cell</button>
</div>

{#if running}
    <button class="btn btn-md variant-filled-error h-10 mb-5" on:click={() => stopFixing()}> Stop</button>
{:else}
    <button class="btn btn-md variant-filled-secondary h-10" on:click={() => fix()}> Napraw</button>
{/if}