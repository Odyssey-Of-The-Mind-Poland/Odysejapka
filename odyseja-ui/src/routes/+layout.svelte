<script lang='ts'>
  import '../app.css';

</script>

<div class="h-screen w-full">
    <slot/>
</div>

<style>

    /* https://fonts.google.com/specimen/Archivo */
    @import url('https://fonts.googleapis.com/css2?family=Archivo:wght@100;300;400;500;600;700;800;900&display=swap');

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
