<script lang="ts">
    import DashboardShell from './components/DashboardShell.svelte';

    let {children} = $props();
</script>

<DashboardShell>
    {@render children()}
</DashboardShell>
