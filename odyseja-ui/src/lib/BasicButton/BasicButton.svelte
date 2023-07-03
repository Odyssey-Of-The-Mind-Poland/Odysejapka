<script lang="ts">

    export let text, icon;

</script>

<button on:click class="hover:bg-white outline outline-1 outline-dark-500 hover:outline-dark-200 px-3 py-1 rounded
    flex justify-center items-center text-dark-500 hover:text-dark-300">
        <span class="pr-2 text-lg">{text}</span>
        <span class="material-symbols-rounded text-md">{icon}</span>
</button>