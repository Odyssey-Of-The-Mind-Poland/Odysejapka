<script>
    import Icon from '@iconify/svelte';
    export let menuItems = []

</script>

<nav class="list-nav flex-auto">
    <ul>
        {#each menuItems as item}
            <li class="mb-4">
                <a href="{item.route}" class="text-left">
                    <Icon icon="{item.icon}" class="text-3xl"/>
                    <span class="text-lg font-semibold">{item.label}</span>
                </a>
            </li>
        {/each}
    </ul>
</nav>