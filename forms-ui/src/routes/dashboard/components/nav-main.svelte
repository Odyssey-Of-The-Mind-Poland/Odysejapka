<script lang="ts">
    import * as Sidebar from "$lib/registry/ui/sidebar";
    import type {Icon} from "@tabler/icons-svelte";

    let {items}: { items: { title: string; url: string; icon?: Icon }[] } = $props();
</script>

<Sidebar.Group>
    <Sidebar.GroupContent>
        <Sidebar.GroupLabel>Administration</Sidebar.GroupLabel>
        <Sidebar.Menu>
            {#each items as item (item.title)}
                <Sidebar.MenuItem>
                    <Sidebar.MenuButton tooltipContent={item.title}>
                        {#snippet child({props})}
                            <a href={item.url} {...props}>
                                <item.icon/>
                                <span>{item.title}</span>
                            </a>
                        {/snippet}
                    </Sidebar.MenuButton>
                </Sidebar.MenuItem>
            {/each}
        </Sidebar.Menu>
    </Sidebar.GroupContent>
</Sidebar.Group>
