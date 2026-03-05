<script lang="ts">
    import * as Sidebar from "$lib/registry/ui/sidebar";
    import NavMain from "./nav-main.svelte";
    import NavUser from "./nav-user.svelte";
    import CitySelector from "./city-selector.svelte";
    import type {ComponentProps} from "svelte";
    import {routes} from "../routes";

    let {...restProps}: ComponentProps<typeof Sidebar.Root> = $props();
</script>

<Sidebar.Root collapsible="none" class="h-auto border-r" {...restProps}>
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
