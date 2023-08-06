<!-- src/components/Sidebar.svelte -->
<script>
  import {logout} from "../../authService";
  import {AppBar, AppShell, LightSwitch, Toast} from "@skeletonlabs/skeleton";
  import Navigation from "$lib/Navigation/Navigation.svelte";
  import logo from '$lib/assets/logo.png'
  import Icon from "@iconify/svelte";

  const menuItems = [
    { label: 'Harmonogram', route: '/panel/timetable', icon: 'ic:round-calendar-view-month' },
    { label: 'Problemy', route: '/panel/problem', icon: 'ic:round-format-list-bulleted' },
    { label: 'Informacje', route: '/panel/info', icon: 'ic:outline-info' },
    { label: 'Sceny', route: '/panel/stage', icon: 'ic:outline-curtains'}
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
          <span class="text-2xl font-semibold text-primary-600">Odyseja Umysłu</span>
        </a>
      </div>
      <Navigation menuItems={menuItems} />
      <div class="flex flex-row justify-between items-center">
        <button type="button" class="btn btn-sm variant-filled-primary w-32 font-semibold" on:click={logout}>
          Wyloguj
        </button>
        <LightSwitch class=""/>
      </div>
    </div>
  </svelte:fragment>
</AppShell>