<!-- src/components/Sidebar.svelte -->
<script>
  import {logout} from "../../authService";
  import Navigation from "$lib/Navigation/Navigation.svelte";
  import Icon from '@iconify/svelte'

  const menuItems = [
    { label: 'Harmonogram', route: '/panel/timetable', icon: 'ic:round-calendar-view-month' },
    { label: 'Problemy', route: '/panel/problem', icon: 'ic:round-format-list-bulleted' },
  ];
</script>

<div class="grid grid-cols-12">

  <div class="col-span-2 border-r border-r-neutral-200">
    <div class="flex h-full flex-col">

      <div class="mt-12 mb-16 text-center flex flex-col items-center">
        <h1 class="text-2xl font-semibold text-dark-500">Odyseja Umysłu</h1>
        <div class="bg-orange-50 px-4 rounded-full outline outline-1 outline-orange-500 mt-2">
          <h2 class="text-orange-500 text-md">Admin</h2>
        </div>
      </div>

      <Navigation menuItems={menuItems} />

      <hr class="h-px mx-6">
      <button on:click={logout} class="group/button my-6 mx-6 py-4 rounded-lg hover:bg-orange-400/20">
        <span class="flex ml-2 items-center">
          <span class="bg-orange-500 h-9 w-9 rounded-full mr-3 flex justify-center items-center">
            <Icon icon="ic:round-log-out" class="text-white text-2xl inline-flex"/>
          </span>
          <span class="text-md font-medium text-dark-500 group-hover/button:text-orange-600">Wyloguj</span>
        </span>
      </button>

    </div>
  </div>

  <div class="col-span-10 min-h-screen">
    <slot />
  </div>

</div>