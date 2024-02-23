<script lang="ts">
    import {getImportStatus, importZsp, stopImport} from "./performanceService.js";
    import {type City, type Progress, Status} from "$lib/types";
    import {city} from "$lib/cityStore";
    import {onDestroy, onMount} from "svelte";
    import {ProgressBar} from "@skeletonlabs/skeleton";

    let zspId = ""
    let currentCity: City

    let importProgress: Progress = {status: Status.STOPPED, progress: 100};
    let intervalId: any = null;

    onMount(() => {
        intervalId = setInterval(() => {
            getImportStatus().then((progress) => {
                importProgress = progress;
            });
        }, 5000);
    });

    onDestroy(() => {
        if (intervalId) {
            clearInterval(intervalId);
        }
    });

    city.subscribe(async curr => {
        currentCity = curr
    });

    async function importHarmonogram() {
        importProgress = {status: Status.RUNNING, progress : 0};
        await importZsp(zspId, currentCity.id)
    }

    async function stop() {
        importProgress = {status: Status.STOPPED, progress : 100};
        await stopImport()
    }


</script>

<div>
    {#if importProgress.status === Status.STOPPED}
        <div class="flex flex-wrap space-x-5 mb-5">
            <div class="flex-grow flex-4 space-x-5">
                <input bind:value={zspId} class="input" placeholder="id ZSP" type="text"/>
            </div>
            <button
                    type="button"
                    class="btn variant-filled-primary h-full"
                    on:click={importHarmonogram}>Importuj harmonogram
            </button>
        </div>
    {:else}
        <button class="btn btn-md variant-filled-error h-10 mb-5" on:click={stop}>Zatrzymaj generowanie</button>
        <ProgressBar label="Progress Bar" value={importProgress.progress} max={100}/>
    {/if}
</div>

