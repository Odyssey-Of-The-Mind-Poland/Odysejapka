<script lang="ts">
    import Editor from '@tinymce/tinymce-svelte';
    import type {Info} from "$lib/types";
    import {post} from "$lib/apiService";
    import {goto} from "$app/navigation";
    import {deleteInfo} from "../../infoService";

    export let data: Info

    function save() {
        saveInfo(data)
        goto('/panel/info')
    }

    async function saveInfo(info: Info) {
        await post(info,'/info', 'Info zapisano pomyślnie')
    }

    async function removeInfo(id: number) {
        await deleteInfo(id)
        goto('/panel/info')
    }

</script>

<h1>{data.infoName}</h1>
<br>
<Editor apiKey="bzbpgl0oh7lp9vuq7avj5zatxnu5daoi8z6gcnum32zkeq4l"
        bind:value="{data.infoText}"/>

<br>

<button
        type="button"
        class="btn btn-md variant-filled-primary"
        on:click={save}>Zapisz
</button>

<button
        type="button"
        class="btn btn-md variant-filled-error"
        on:click={removeInfo(data.id)}>Usuń
</button>