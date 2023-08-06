<script>
    import Icon from '@iconify/svelte';
    export let menuItems = []

</script>

<nav class="flex-auto">
    <ul class="mx-2">
        {#each menuItems as item}
            <li class="rounded-md mt-4 first:mt-0 hover:bg-primary-500/10">
                <a href="{item.route}" class="text-left px-4 py-4 flex items-center text-dark-500">
                    <Icon icon="{item.icon}" class="text-3xl inline-flex"/>
                    <span class="text-lg font-normal pl-2">{item.label}</span>
                </a>
            </li>
        {/each}
    </ul>
</nav>
