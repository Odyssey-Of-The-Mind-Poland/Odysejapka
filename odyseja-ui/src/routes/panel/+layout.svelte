<!-- src/components/Sidebar.svelte -->
<script>
  import {logout} from "../../authService";
  import {AppShell, Toast} from "@skeletonlabs/skeleton";
  import Navigation from "$lib/Navigation/Navigation.svelte";
  import logo from '$lib/assets/logo.png'
  import Icon from '@iconify/svelte'

  const menuItems = [
    { label: 'Panel główny', route: '/panel', icon: 'ic:round-space-dashboard'},
    { label: 'Harmonogram', route: '/panel/timetable', icon: 'ic:round-calendar-view-month' },
    { label: 'Problemy', route: '/panel/problem', icon: 'ic:round-format-list-bulleted' },
  ];
</script>

<Toast />

<AppShell slotSidebarLeft="bg-surface-500/5 w-64 p-4 mx-4 my-4 outline rounded-lg">

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

      <hr class="h-px my-4 ">
      <button on:click={logout} class="py-2 rounded-lg hover:bg-orange-400/20">
        <div class="flex ml-2 items-center">
          <div class="bg-orange-400 h-9 w-9 rounded-full mr-3 flex justify-center items-center">
            <Icon icon="ic:round-log-out" class="text-white text-2xl"/>
          </div>
          <span class="text-md font-semibold mb-1">Wyloguj</span>
        </div>
      </button>
    </div>
  </svelte:fragment>
</AppShell>