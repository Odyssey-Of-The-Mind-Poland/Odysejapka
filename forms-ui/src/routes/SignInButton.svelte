<script lang="ts">
    import {SignIn, SignOut} from "@auth/sveltekit/components";
    import {page} from "$app/state"
    import {Button} from "$lib/components/ui/button";

    let session = $derived(page.data.session)
</script>

{#if session}
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
