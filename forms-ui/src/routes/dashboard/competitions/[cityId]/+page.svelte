<script lang="ts">
    import {page} from "$app/state";
    import {goto} from "$app/navigation";
    import {onMount} from "svelte";
    import {currentUser} from "$lib/userStore";
    import {get} from "svelte/store";

    let cityId = $derived(Number(page.params.cityId));

    onMount(() => {
        const user = get(currentUser);
        const isSpontan = user?.roles.includes('SPONTAN') ?? false;
        const target = isSpontan
            ? `/dashboard/competitions/${cityId}/spontany`
            : `/dashboard/competitions/${cityId}/dt`;
        goto(target, {replaceState: true});
    });
</script>
