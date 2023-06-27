<script>
    import Icon from '@iconify/svelte';
    export let menuItems = []

</script>

<nav class="list-nav flex-auto">
    <ul>
        {#each menuItems as item}
            <li><a href="{item.route}">
                <Icon icon="{item.icon}" class="text-4xl "/>
                {item.label}
            </a></li>
        {/each}
    </ul>
</nav>