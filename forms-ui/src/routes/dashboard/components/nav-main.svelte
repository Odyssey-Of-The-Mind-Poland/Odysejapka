<script lang="ts">
    import * as Sidebar from "$lib/registry/ui/sidebar";
    import * as Collapsible from "$lib/components/ui/collapsible";
    import {currentUser} from "$lib/userStore";
    import {selectedCity} from "$lib/cityStore";
    import {page} from "$app/state";
    import type {NavItem} from "../routes";
    import IconChevronRight from "@tabler/icons-svelte/icons/chevron-right";

    let {items}: { items: NavItem[] } = $props();

    let user = $derived($currentUser);
    let city = $derived($selectedCity);

    let visibleItems = $derived(
        items.filter((item: NavItem) => {
            if (!item.requiredRole && !item.requiredRoles) return true;
            if (item.requiredRoles) {
                return item.requiredRoles.some(role => user?.roles.includes(role)) ?? false;
            }
            return user?.roles.includes(item.requiredRole!) ?? false;
        })
    );

    function isItemActive(item: NavItem): boolean {
        return page.url.pathname.startsWith(item.url);
    }

    function childUrl(item: NavItem, childId: string): string {
        if (!city) return item.url;
        return `${item.url}/${city.id}/${childId}`;
    }

    function isChildActive(item: NavItem, childId: string): boolean {
        if (!city) return false;
        return page.url.pathname.startsWith(`${item.url}/${city.id}/${childId}`);
    }
</script>

<Sidebar.Group>
    <Sidebar.GroupContent>
        <Sidebar.Menu>
            {#each visibleItems as item (item.title)}
                {#if item.needsCity && item.children && city}
                    <Collapsible.Root open={isItemActive(item)} class="group/collapsible">
                        <Sidebar.MenuItem>
                            <Collapsible.Trigger>
                                {#snippet child({props})}
                                    <Sidebar.MenuButton tooltipContent={item.title} {...props}>
                                        <item.icon/>
                                        <span>{item.title}</span>
                                        <IconChevronRight
                                            class="ml-auto size-4 transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90"
                                        />
                                    </Sidebar.MenuButton>
                                {/snippet}
                            </Collapsible.Trigger>
                            <Collapsible.Content>
                                <Sidebar.MenuSub>
                                    {#each item.children as navChild (navChild.id)}
                                        <Sidebar.MenuSubItem>
                                            <Sidebar.MenuSubButton isActive={isChildActive(item, navChild.id)}>
                                                {#snippet child({props})}
                                                    <a href={childUrl(item, navChild.id)} {...props}>
                                                        <span>{navChild.label}</span>
                                                    </a>
                                                {/snippet}
                                            </Sidebar.MenuSubButton>
                                        </Sidebar.MenuSubItem>
                                    {/each}
                                </Sidebar.MenuSub>
                            </Collapsible.Content>
                        </Sidebar.MenuItem>
                    </Collapsible.Root>
                {:else}
                    <Sidebar.MenuItem>
                        <Sidebar.MenuButton tooltipContent={item.title} isActive={isItemActive(item)}>
                            {#snippet child({props})}
                                <a href={item.url} {...props}>
                                    <item.icon/>
                                    <span>{item.title}</span>
                                </a>
                            {/snippet}
                        </Sidebar.MenuButton>
                    </Sidebar.MenuItem>
                {/if}
            {/each}
        </Sidebar.Menu>
    </Sidebar.GroupContent>
</Sidebar.Group>
