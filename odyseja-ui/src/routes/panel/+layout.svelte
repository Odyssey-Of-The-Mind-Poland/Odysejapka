<!-- src/components/Sidebar.svelte -->
<script>
  import {logout} from "../../authService";
  import {AppBar, AppShell, LightSwitch, Toast} from "@skeletonlabs/skeleton";
  import Navigation from "$lib/Navigation/Navigation.svelte";
  import logo from '$lib/assets/logo.png'

  const menuItems = [
    { label: 'Harmonogram', route: '/panel/timetable', icon: 'ic:round-calendar-view-month' },
    { label: 'Problemy', route: '/panel/problem', icon: 'ic:round-format-list-bulleted' },
    { label: 'Informacje', route: '/panel/info', icon: 'ic:round-format-list-bulleted' },
  ];
</script>

<Toast />

<AppShell slotSidebarLeft="bg-surface-500/5 w-64 p-4">

  <div class="p-6">
    <slot />
  </div>
  <svelte:fragment slot="sidebarLeft">
    <div class="flex h-full flex-col">
      <div class="mt-4 mb-12 text-center">
        <a href="/panel">
          <img src="{logo}" alt="Logo Odysei Umysłu" class="h-2/5 m-auto"/>
          <span class="text-2xl font-semibold text-primary-600">Odyseja Umysłu</span>
        </a>
      </div>
      <Navigation menuItems={menuItems} />
      <button type="button" class="btn btn-sm variant-filled-primary" on:click={logout}>
        Logout
      </button>
      <LightSwitch class=""/>
    </div>
  </svelte:fragment>
</AppShell>