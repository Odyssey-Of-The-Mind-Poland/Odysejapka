<script lang="ts">
    import {SignOut} from "@auth/sveltekit/components";
    import {page} from "$app/state"
    import {Button} from "$lib/components/ui/button";
    import {currentUser} from "$lib/userStore";
    import {apiFetch} from "$lib/api";
    import type {CurrentUser} from "$lib/userStore";
    import {onMount} from "svelte";

    let session = $derived(page.data.session);
    let user = $derived($currentUser);
    let checking = $state(true);

    onMount(async () => {
        currentUser.set(null);
        try {
            const me = await apiFetch<CurrentUser>('/api/v1/users/me');
            if (me?.id) {
                currentUser.set(me);
            }
        } catch {
            // Not authenticated — leave currentUser as null
        }
        checking = false;
    });
</script>

{#if checking}
    <!-- loading -->
{:else if user}
    <span class="signedInText">
      {#if session?.user?.image}
        <img src={session.user.image} alt="User Avatar" class="w-8 h-8 rounded-full" />
      {/if}
    </span>
    {#if session?.user}
        <!-- Auth0 user: use Auth.js sign-out -->
        <SignOut>
            <input type="hidden" name="callbackUrl" value="/" />
            <Button slot="submitButton" class="buttonPrimary">Sign out</Button>
        </SignOut>
    {:else}
        <!-- Local user: clear backend token cookie -->
        <form method="POST" action="/auth/logout">
            <Button type="submit" class="buttonPrimary">Sign out</Button>
        </form>
    {/if}
{:else}
    <span class="notSignedInText">You are not signed in</span>
    <a href="/auth/login">
        <Button class="buttonPrimary">Sign in</Button>
    </a>
{/if}
