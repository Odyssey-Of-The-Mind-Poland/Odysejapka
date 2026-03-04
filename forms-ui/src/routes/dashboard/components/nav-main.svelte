<script lang="ts">
    import * as Sidebar from "$lib/registry/ui/sidebar";
    import {currentUser} from "$lib/userStore";
    import type {NavItem} from "../routes";

    let {items}: { items: NavItem[] } = $props();

    let user = $derived($currentUser);

    let visibleItems = $derived(
        items.filter((item: NavItem) => {
            if (!item.requiredRole && !item.requiredRoles) return true;
            if (item.requiredRoles) {
                return item.requiredRoles.some(role => user?.roles.includes(role)) ?? false;
            }
            return user?.roles.includes(item.requiredRole!) ?? false;
        })
    );
</script>

<Sidebar.Group>
    <Sidebar.GroupContent>
        <Sidebar.Menu>
            {#each visibleItems as item (item.title)}
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
