<script lang="ts">
    import '../app.css';
    import Header from './Header.svelte';
    import {QueryClient, QueryClientProvider} from "@tanstack/svelte-query";

    const queryClient = new QueryClient()
    let {children} = $props();
</script>

<QueryClientProvider client={queryClient}>
    {@render children()}
</QueryClientProvider>

