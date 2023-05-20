<!-- src/components/Sidebar.svelte -->
<script>
  import {logout} from "../../authService";
  import {AppBar, AppShell} from "@skeletonlabs/skeleton";

  const menuItems = [
    { label: 'Harmonogram', route: '/panel/timetable' },
    { label: 'Problemy', route: '/panel/problem' },
  ];
</script>

<AppShell slotSidebarLeft="bg-surface-500/5 w-56 p-4">
  <svelte:fragment slot="header">
    <AppBar>
      <svelte:fragment slot="lead">
        <strong class="text-xl uppercase">Odyseja Umysłu</strong>
      </svelte:fragment>
    </AppBar>
  </svelte:fragment>

  <div class="p-6">
    <slot />
  </div>
  <svelte:fragment slot="sidebarLeft">
    <div class="flex h-full flex-col">
      <nav class="list-nav flex-auto">
        <ul>
          {#each menuItems as item}
            <li><a href="{item.route}">{item.label}</a></li>
          {/each}
        </ul>
      </nav>

      <button type="button" class="btn btn-sm variant-filled-primary" on:click={logout}>
        Logout
      </button>
    </div>
  </svelte:fragment>
</AppShell>