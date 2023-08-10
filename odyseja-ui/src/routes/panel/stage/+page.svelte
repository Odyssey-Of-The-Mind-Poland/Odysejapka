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

    let editToggled = false
    function toggleEdit() {
        editToggled = !editToggled;
        editToggled = editToggled;
    }

    function cancelChanges() {
        toggleEdit();
        data = JSON.parse(JSON.stringify(initialData));
    }

</script>

    <div class="mt-4 mx-4 flex justify-between items-center">
        <h2 class="mb-6">Problemy</h2>
        <button type="button" on:click={toggleEdit} class="btn btn-md variant-filled-primary"
                disabled='{editToggled}'>Edytuj</button>
    </div>
    <section class="p-4 flex flex-wrap">
        {#each data.stages as stage}
            <label class="label p-2 w-1/3">
                <span>Scena nr. {stage.id}</span>
                <input class="input" type="text" placeholder="Input" bind:value={stage.name}/>
            </label>
        {/each}
    </section>

    <footer class="pl-8">
        <button type="button" on:click={save} disabled='{!isChanged}' class="btn btn-md variant-filled-secondary">
            Zapisz
        </button>
        <button type="button" on:click={cancelChanges} disabled='{!editToggled}' class="btn btn-md variant-ringed-surface">
            Anuluj
        </button>
    </footer>
