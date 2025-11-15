<script lang="ts">
    import AppSidebar from "./components/app-sidebar.svelte";
    import SiteHeader from "./components/site-header.svelte";
    import * as Sidebar from "$lib/registry/ui/sidebar/index.js";
    import {goto} from "$app/navigation";
    import {routes} from "./routes";
    import {onMount, onDestroy} from "svelte";
    import {session as sessionStore} from "$lib/sessionStore";
    import {get} from "svelte/store";

    import jwtDecode from "jwt-decode";

    export let data;

    function isSessionActive(token?: string): boolean {
        if (!token) return false;
        try {
            type JwtPayload = { exp?: number };
            const {exp} = jwtDecode<JwtPayload>(token);
            return !!exp && exp > Math.floor(Date.now() / 1000);
        } catch {
            return false;
        }
    }

    function handleSessionExpiry() {
        sessionStore.set(null);
        goto("/");
    }

    onMount(() => {
        if (isSessionActive(data.session?.accessToken)) {
            sessionStore.set(data.session);
        } else {
            handleSessionExpiry();
        }

        const interval = setInterval(() => {
            const token = get(sessionStore)?.accessToken;
            if (!isSessionActive(token)) {
                handleSessionExpiry();
            }
        }, 10_000);

        onDestroy(() => clearInterval(interval));
    });
</script>

<Sidebar.Provider
        class="md:flex"
        style="--sidebar-width: calc(var(--spacing) * 64); --header-height: calc(var(--spacing) * 12 + 1px);"
>
    <AppSidebar variant="sidebar"/>
    <Sidebar.Inset>
        <SiteHeader/>
        <slot/>
    </Sidebar.Inset>
</Sidebar.Provider>


