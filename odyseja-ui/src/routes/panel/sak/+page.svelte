<script lang="ts">
    import {onDestroy, onMount} from "svelte";
    import {ProgressBar} from "@skeletonlabs/skeleton";
    import {type Progress, type SakRequest, Status} from "$lib/types";
    import {getSakStatus, runSak, stopSakRun} from "./sakService";

    export let data: SakRequest;

    let folderId = data.templatesFolderId;
    let zspId = data.zspId;
    let sakProgress: Progress = {status: Status.STOPPED, progress: 100};
    let intervalId: any = null;

    onMount(() => {
        intervalId = setInterval(() => {
            getSakStatus().then((progress) => {
                sakProgress = progress;
            });
        }, 5000);
    });

    onDestroy(() => {
        if (intervalId) {
            clearInterval(intervalId);
        }
    });

    function startSak() {
        runSak({templatesFolderId: folderId, zspId: zspId} as SakRequest);
        sakProgress = {status: Status.RUNNING, progress: 0};
    }

    function stopSak() {
        sakProgress = {status: Status.STOPPED, progress: 0};
        stopSakRun();
    }
</script>

<h1 class="m-5">SAK</h1>

<div>
    <input bind:value={folderId} class="input m-5" placeholder="Folder z arkuszami"
           type="text"/>
    <input bind:value={zspId} class="input m-5" placeholder="ZSP ID"
           type="text"/>
</div>

<div class="m-5">
    {#if sakProgress.status === Status.STOPPED}
        <button class="btn btn-md variant-filled-secondary h-10" on:click={startSak}>Generuj arkusze</button>
    {:else}
        <button class="btn btn-md variant-filled-error h-10 mb-5" on:click={stopSak}>Zatrzymaj generowanie</button>
        <ProgressBar label="Progress Bar" value={sakProgress.progress} max={100}/>
    {/if}
</div>
