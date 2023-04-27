<script lang='ts'>
  import '../theme.postcss';
  import '@skeletonlabs/skeleton/styles/all.css';
  import '../app.postcss';
  import { ProgressRadial } from '@skeletonlabs/skeleton';

  import { checkAuth } from '../authHelper';
  import { page } from '$app/stores';
  import { get } from 'svelte/store';

  import { onMount } from 'svelte';

  let authenticated;

  onMount(async () => {
    authenticated = await checkAuth(get(page));
    console.log('authenticated: ', authenticated);
  });
</script>


{#await authenticated then auth}
  {#if auth !== undefined && auth.authenticated}
    <slot/>
  {:else}
    <div class="container h-full mx-auto flex justify-center items-center">
      <ProgressRadial ... stroke={100} meter="stroke-primary-500" track="stroke-primary-500/30"/>
    </div>
  {/if}
{/await}
