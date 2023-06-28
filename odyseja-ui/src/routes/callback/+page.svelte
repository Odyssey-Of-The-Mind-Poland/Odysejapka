<script>
  import {handleAuthentication} from '../../authService'
  import {onMount} from "svelte";
  import {goto} from "$app/navigation";

  onMount(async () => {
      try {
          await handleAuthentication();
          await goto('/panel');
      } catch (error) {
          console.log(error);
          await goto('/');
      }
  });

</script>