<script>
  import Login from "../components/Login.svelte";

  import {checkAuth} from '../authHelper.js';
  import {page} from '$app/stores';
  import { get } from 'svelte/store';

  import {onMount} from 'svelte';

  let authenticated;

  onMount(async () => {
    authenticated = await checkAuth(get(page));
    console.log('authenticated: ', authenticated)
  });

</script>

{#await authenticated then auth}
  {#if auth !== undefined && auth.authenticated}
    <slot/>
  {:else}
    <Login/>
  {/if}
{/await}