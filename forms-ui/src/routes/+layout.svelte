<script lang="ts">
    import '../app.css';
    import Header from './Header.svelte';
    import {QueryClient, QueryClientProvider} from "@tanstack/svelte-query";
    import {Toaster} from "$lib/components/ui/sonner";

    const queryClient = new QueryClient()
    let {children} = $props();
</script>

<QueryClientProvider client={queryClient}>
    <Toaster/>
    {@render children()}
</QueryClientProvider>

