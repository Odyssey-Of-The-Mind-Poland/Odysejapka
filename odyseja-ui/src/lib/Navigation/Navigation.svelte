<script>

    export let menuItems = []

</script>

<nav class="list-nav flex-auto">
    <ul>
        {#each menuItems as item}
            <li><a href="{item.route}">{item.label}</a></li>
        {/each}
    </ul>
</nav>