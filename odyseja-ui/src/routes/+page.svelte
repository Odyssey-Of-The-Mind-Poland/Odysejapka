<script>
  import {login} from '../authService'

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
