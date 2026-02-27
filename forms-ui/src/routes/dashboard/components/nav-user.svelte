<script lang="ts">
    import DotsVerticalIcon from "@tabler/icons-svelte/icons/dots-vertical";
    import LogoutIcon from "@tabler/icons-svelte/icons/logout";
    import * as Sidebar from "$lib/registry/ui/sidebar";
    import {page} from "$app/state";
    import * as Avatar from "$lib/registry/ui/avatar/index.js";
    import * as DropdownMenu from "$lib/registry/ui/dropdown-menu/index.js";
    import {currentUser} from "$lib/userStore";

    const sidebar = Sidebar.useSidebar();

    let session = $derived(page.data.session);
    let user = $derived($currentUser);

    function getInitials(name: string | null | undefined): string {
        if (!name) return '?';
        return name.split(' ').map((n) => n[0]).join('').toUpperCase().slice(0, 2);
    }
</script>

<Sidebar.Menu>
    <Sidebar.MenuItem>
        <DropdownMenu.Root>
            <DropdownMenu.Trigger>
                {#snippet child({props})}
                    <Sidebar.MenuButton
                            {...props}
                            size="lg"
                            class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
                    >
                        <Avatar.Root class="h-8 w-8 rounded-lg">
                            <Avatar.Image src={session?.user?.image} alt={user?.username ?? session?.user?.name}/>
                            <Avatar.Fallback class="rounded-lg">{getInitials(user?.username ?? session?.user?.name)}</Avatar.Fallback>
                        </Avatar.Root>
                        <div class="grid flex-1 text-left text-sm leading-tight">
                            <span class="truncate font-medium">{user?.username ?? session?.user?.name}</span>
                            <span class="text-muted-foreground truncate text-xs">
								{user?.email ?? session?.user?.email}
							</span>
                        </div>
                        <DotsVerticalIcon class="ml-auto size-4"/>
                    </Sidebar.MenuButton>
                {/snippet}
            </DropdownMenu.Trigger>
            <DropdownMenu.Content
                    class="w-(--bits-dropdown-menu-anchor-width) min-w-56 rounded-lg"
                    side={sidebar.isMobile ? "bottom" : "right"}
                    align="end"
                    sideOffset={4}
            >
                <DropdownMenu.Item>
                    <form method="POST" action="/auth/logout" class="contents">
                        <button type="submit" class="flex items-center gap-2 w-full">
                            <LogoutIcon/>
                            Wyloguj
                        </button>
                    </form>
                </DropdownMenu.Item>
            </DropdownMenu.Content>
        </DropdownMenu.Root>
    </Sidebar.MenuItem>
</Sidebar.Menu>
