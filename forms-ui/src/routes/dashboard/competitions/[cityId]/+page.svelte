<script lang="ts">
    import {page} from "$app/state";
    import {goto} from "$app/navigation";
    import {onMount} from "svelte";

    let cityId = $derived(Number(page.params.cityId));

    onMount(() => {
        goto(`/dashboard/competitions/${cityId}/dt`, {replaceState: true});
    });
</script>
