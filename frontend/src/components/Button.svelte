<script>
    export let onClick;

    function handleClick() {
        onClick();
    }
</script>

<style>

    .button {
        background-color: #FCC573;
        border: none;
        color: #883600;
        font-size: 1.2rem;
        padding: 0.5rem 1rem;
        border-radius: 4px;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .button:hover {
        background-color: #C86B00;
    }
</style>

<button class="button" on:click={handleClick}>
    <slot />
</button>