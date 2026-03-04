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
        const allProtectedRoutes = [
            ...routes.navMain
                .filter(r => r.requiredRole || r.requiredRoles)
                .map(r => ({
                    url: r.url,
                    requiredRoles: r.requiredRoles ?? (r.requiredRole ? [r.requiredRole] : []),
                })),
            ...routes.adminRoutes.map(r => ({url: r.url, requiredRoles: [r.requiredRole]})),
        ];
        const matchedRoute = allProtectedRoutes.find((r) => currentPath.startsWith(r.url));
        if (matchedRoute && !matchedRoute.requiredRoles.some(role => user.roles.includes(role))) {
            goto("/dashboard/competitions");
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
            goto("/");
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
