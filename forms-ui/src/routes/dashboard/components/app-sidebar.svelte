<script lang="ts">
    import * as Sidebar from "$lib/registry/ui/sidebar";
    import {useSidebar} from "$lib/registry/ui/sidebar";
    import {afterNavigate} from "$app/navigation";
    import NavMain from "./nav-main.svelte";
    import NavUser from "./nav-user.svelte";
    import CitySelector from "./city-selector.svelte";
    import type {ComponentProps} from "svelte";
    import {routes} from "../routes";

    let {...restProps}: ComponentProps<typeof Sidebar.Root> = $props();

    // Below `md` the sidebar renders as an overlay sheet. Closing it after each
    // navigation stops it from covering the page it just linked to; doing it
    // here rather than per-link also covers the city selector and user menu.
    const sidebar = useSidebar();

    afterNavigate(() => {
        if (sidebar.openMobile) sidebar.setOpenMobile(false);
    });
</script>

<Sidebar.Root collapsible="offcanvas" class="border-r" {...restProps}>
    <Sidebar.Header class="border-b">
        <Sidebar.Menu>
            <Sidebar.MenuItem>
                <Sidebar.MenuButton class="data-[slot=sidebar-menu-button]:!p-1.5">
                    {#snippet child({props})}
                        <a href="##" {...props}>
                            <span class="text-base font-semibold">Odyseja Umysłu</span>
                        </a>
                    {/snippet}
                </Sidebar.MenuButton>
            </Sidebar.MenuItem>
        </Sidebar.Menu>
    </Sidebar.Header>
    <CitySelector/>
    <Sidebar.Content>
        <NavMain items={routes.navMain}/>
    </Sidebar.Content>
    <Sidebar.Footer>
        <NavUser/>
    </Sidebar.Footer>
</Sidebar.Root>
