<script lang="ts">
    import SignInButton from "./SignInButton.svelte";
    import {page} from '$app/state';

    $: session = page.data.session;
</script>

<nav class="flex justify-between items-center p-4 border-b shadow-sm">
    <div class="flex items-center gap-6">
        <a class="text-lg font-bold" href="/">Odyseja Formularze</a>

        {#if session?.user}
            <a href="/dashboard" class="text-sm font-medium text-gray-700 hover:text-black transition-colors">
                Dashboard
            </a>
        {/if}
    </div>

    <div class="flex items-center gap-4">
        <SignInButton/>
    </div>
</nav>
