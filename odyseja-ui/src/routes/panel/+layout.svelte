<!-- src/components/Sidebar.svelte -->
<script>
  import {logout} from "../../authService";
  import {AppBar, AppShell, LightSwitch, Toast} from "@skeletonlabs/skeleton";
  import Navigation from "$lib/Navigation/Navigation.svelte";

  const menuItems = [
    { label: 'Harmonogram', route: '/panel/timetable', icon: 'ic:round-calendar-view-month' },
    { label: 'Problemy', route: '/panel/problem', icon: 'ic:round-format-list-bulleted' },
  ];
</script>

<Toast />

<AppShell slotSidebarLeft="bg-surface-500/5 w-56 p-4">
  <svelte:fragment slot="header">
    <AppBar>
      <svelte:fragment slot="lead">
        <strong class="text-2xl">Odyseja Umysłu</strong>
      </svelte:fragment>
        <LightSwitch class="float-right"/>
    </AppBar>
  </svelte:fragment>

  <div class="p-6">
    <slot />
  </div>
  <svelte:fragment slot="sidebarLeft">
    <div class="flex h-full flex-col">
        <Navigation menuItems={menuItems} />
      <button type="button" class="btn btn-sm variant-filled-primary" on:click={logout}>
        Logout
      </button>
    </div>
  </svelte:fragment>
</AppShell>