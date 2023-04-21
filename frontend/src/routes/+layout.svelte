<script>
    import { checkAuth } from '../authHelper.js';
    import { page } from '$app/stores';


    import { onMount } from 'svelte';
    import {goto} from "$app/navigation";

    onMount(async () => {
        const {authenticated} = await checkAuth(page);

        if (authenticated) {
            await goto('/panel');
            return;
        }

        await goto('/');
    });
</script>

<slot />