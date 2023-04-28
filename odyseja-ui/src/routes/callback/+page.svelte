<script>
  import {handleAuthentication} from '../../authService'
  import {onMount} from "svelte";
  import {goto} from "$app/navigation";

  onMount(() => {
    handleAuthentication()
    .then(async () => {
      await goto('/panel');
    })
    .catch(async (error) => {
      console.log(error)
      await goto('/');
    });
  });

</script>