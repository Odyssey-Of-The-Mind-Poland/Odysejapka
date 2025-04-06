<script lang="ts">
  import {login, logout} from '../authService'
  import Token from '../token';
  import { onMount, onDestroy } from 'svelte';
  import { goto } from '$app/navigation';

  let isExpired = false;
  let checkInterval: number;

  function checkToken() {
    const token = new Token(document.cookie.split('; ').find(row => row.startsWith('access_token='))?.split('=')[1]);
    if (token) {
      isExpired = token.isExpired();
      
      if (isExpired) {
        logout();
        goto('/');
      }
    }
  }

  onMount(() => {
    checkToken();
    checkInterval = window.setInterval(checkToken, 10000);
  });

  onDestroy(() => {
    if (checkInterval) {
      clearInterval(checkInterval);
    }
  });

  function handleLogin() {
    login();
  }
</script>

<div class="container h-full mx-auto flex justify-center items-center">
  <div class="space-y-10 text-center">
    <div class="card p-4">
      <section><img src="logo.png" alt="logo Odyseja Umysłu"></section>
      <footer class="p-4 card-footer">
        <button type="button" class="btn btn-xl variant-filled-primary" on:click={handleLogin}>
          Zaloguj
        </button>
      </footer>
    </div>
  </div>
</div>
