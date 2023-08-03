<script lang="ts">
    import { saveStages } from "../apiService";
    import type { Stages } from "../types"

export let data: Stages;

    let initialData = JSON.parse(JSON.stringify(data))

    $: isChanged = JSON.stringify(data) !== JSON.stringify(initialData);

    function save() {
        saveStages(data);
        initialData = JSON.parse(JSON.stringify(data))
    }

</script>

    <section class="p-4">
        {#each data.stages as stage}
            <label class="label m-4">
                <span>Scena nr. {stage.id}</span>
                <input class="input" type="text" placeholder="Input" bind:value={stage.name}/>
            </label>
        {/each}
    </section>

    <footer class="pl-8">
        <button type="button" on:click={save} disabled='{!isChanged}' class="btn btn-md variant-filled-primary">
            Zapisz
        </button>
    </footer>