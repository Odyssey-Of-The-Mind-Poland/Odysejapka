<script lang="ts">
    import {SignIn, SignOut} from "@auth/sveltekit/components";
    import {page} from "$app/state"
    import {Button} from "$lib/components/ui/button";
    import {currentUser} from "$lib/userStore";
    import {session as sessionStore} from "$lib/sessionStore";
    import {apiFetch} from "$lib/api";
    import type {CurrentUser} from "$lib/userStore";
    import {onMount} from "svelte";

    let session = $derived(page.data.session);
    let user = $derived($currentUser);
    let checking = $state(true);

    onMount(async () => {
        if (session?.accessToken) {
            sessionStore.set(session);
            try {
                const me = await apiFetch<CurrentUser>('/api/v1/users/me');
                currentUser.set(me);
            } catch {
                currentUser.set(null);
            }
        }
        checking = false;
    });
</script>

{#if checking}
    <!-- loading -->
{:else if user}
    <span class="signedInText">
      <img src={session?.user?.image} alt="User Avatar" class="w-8 h-8 rounded-full" />
    </span>
    <SignOut>
        <Button slot="submitButton" class="buttonPrimary">Sign out</Button>
    </SignOut>
{:else}
    <span class="notSignedInText">You are not signed in</span>
    <SignIn>
        <Button slot="submitButton" class="buttonPrimary">Sign in</Button>
    </SignIn>
{/if}
