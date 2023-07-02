<script>
    import Icon from '@iconify/svelte';
    export let menuItems = []

</script>

<nav class="flex-auto">
    <ul class="mx-6">
        {#each menuItems as item}
            <li class="rounded-md mt-6 first:mt-0 hover:outline hover:outline-1 hover:outline-dark-500">
                <a href="{item.route}" class="text-left px-4 py-4 flex items-center text-dark-500">
                    <Icon icon="{item.icon}" class="text-3xl inline-flex"/>
                    <span class="text-lg font-normal pl-2">{item.label}</span>
                </a>
            </li>
        {/each}
    </ul>
</nav>
