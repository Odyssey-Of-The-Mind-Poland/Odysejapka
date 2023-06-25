<script lang='ts'>
  import '../theme.postcss';
  import '@skeletonlabs/skeleton/styles/all.css';
  import '../app.postcss';

</script>


<slot/>

<style>

    :global(dialog) {
        transition: all 2s;
        color: inherit;
    }

    :global(dialog::backdrop) {
        background: linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.7));
        animation: fade-in 1s;
    }

    @keyframes fade-in {
        from {
            opacity: 0;
        }
        to {
            opacity: 1;
        }
    }
</style>
