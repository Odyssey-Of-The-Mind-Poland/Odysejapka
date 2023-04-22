<!-- src/components/Sidebar.svelte -->
<script>
  import { goto } from '$app/navigation';
  import {logout} from "../../authService.js";
  import Button from "../../components/Button.svelte";

  const menuItems = [
    { label: 'Harmonogram', route: '/panel/timetable' },
  ];

  function navigateTo(route) {
    goto(route);
  }
</script>

<style>
  .sidebar {
    background-color: #292B2D;
    width: 240px;
    height: 100vh;
    padding-left: 1rem;
    padding-right: 1rem;
    position: fixed;
    display: flex;
    flex-direction: column;
  }


  .app-name {
    font-size: 1.5rem;
    color: #FCC573;
    margin-bottom: 1rem;
  }

  .menu-items {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .menu-item {
    cursor: pointer;
    padding: 0.5rem 1rem;
    color: #FFF;
    border-radius: 4px;
    transition: background-color 0.2s;
  }

  .menu-item:hover {
    background-color: #FF8800;
  }

  .main-content {
    display: flex;
    flex-direction: column;
    flex: 1;
  }

  .main-container {
    display: flex;
  }

  .content {
    margin-left: 300px;
    padding: 1rem;
    width: 100%;
  }
</style>


<div class="main-container">
  <div class="sidebar">
    <div class="main-content">
      <h1 class="app-name">OdysejaAdmin</h1>
      <div class="menu-items">
        {#each menuItems as item}
          <div class="menu-item" on:click={() => navigateTo(item.route)}>
            {item.label}
          </div>
        {/each}
      </div>
    </div>
    <Button class="menu-item logout-button" onClick={logout}>Logout</Button>
  </div>
  <div class="content">
    <slot />
  </div>
</div>