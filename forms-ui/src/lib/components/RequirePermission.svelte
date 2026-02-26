<script lang="ts">
    import {currentUser, type Role} from "$lib/userStore";
    import type {Snippet} from "svelte";

    let {role, children}: { role: Role; children: Snippet } = $props();

    let user = $derived($currentUser);
    let hasPermission = $derived(user?.roles.includes(role) ?? false);
</script>

{#if hasPermission}
    {@render children()}
{/if}
