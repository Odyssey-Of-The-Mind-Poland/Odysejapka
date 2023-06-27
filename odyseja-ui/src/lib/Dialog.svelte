<script lang="ts">
    export let dialog: HTMLDialogElement;
</script>

<dialog bind:this={dialog} class="card p-10 w-1/2">
    <button class="absolute top-0 right-0 m-2" type="button" on:click={() => dialog.close()}>
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" class="h-6 w-6">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
    </button>

    <slot/>
</dialog>

