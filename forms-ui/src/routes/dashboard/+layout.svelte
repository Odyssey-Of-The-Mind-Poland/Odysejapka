<script lang="ts">
    import AppSidebar from "./components/app-sidebar.svelte";
    import SiteHeader from "./components/site-header.svelte";
    import * as Sidebar from "$lib/registry/ui/sidebar/index.js";
    import {goto} from "$app/navigation";
    import {page} from "$app/state";
    import {onMount} from "svelte";
    import {currentUser} from "$lib/userStore";
    import {apiFetch} from "$lib/api";
    import type {CurrentUser} from "$lib/userStore";
    import {routes} from "./routes";

    function checkRouteAccess(user: CurrentUser) {
        const currentPath = page.url.pathname;
        const matchedRoute = routes.navMain.find((r) => currentPath.startsWith(r.url));
        if (matchedRoute?.requiredRole && !user.roles.includes(matchedRoute.requiredRole)) {
            goto("/dashboard/teams");
        }
    }

    async function fetchCurrentUser() {
        try {
            const user = await apiFetch<CurrentUser>('/api/v1/users/me');
            currentUser.set(user);
            checkRouteAccess(user);
        } catch {
            // Token expired or invalid — redirect to login
            currentUser.set(null);
            goto("/auth/login");
        }
    }

    onMount(() => {
        fetchCurrentUser();
    });
</script>

<Sidebar.Provider
        class="md:flex"
        style="--sidebar-width: calc(var(--spacing) * 64); --header-height: calc(var(--spacing) * 12 + 1px);"
>
    <AppSidebar variant="sidebar"/>
    <Sidebar.Inset>
        <SiteHeader/>
        <div class="p-4 h-full">
            <slot/>
        </div>
    </Sidebar.Inset>
</Sidebar.Provider>
