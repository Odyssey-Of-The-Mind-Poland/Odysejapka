
<script lang="ts">
    import {logout} from "../../authService";
    import {AppShell, LightSwitch, Toast} from "@skeletonlabs/skeleton";
    import Navigation from "$lib/Navigation/Navigation.svelte";
    import type {Cities} from "$lib/types.js";
    import {city} from "$lib/cityStore.js";

    const menuItems = [
        {label: 'Harmonogram', route: '/panel/timetable', icon: 'ic:round-calendar-view-month'},
        {label: 'Problemy', route: '/panel/problem', icon: 'ic:round-format-list-bulleted'},
        {label: 'Informacje', route: '/panel/info', icon: 'ic:outline-info'},
        {label: 'Sceny', route: '/panel/stage', icon: 'ic:outline-curtains'},
        {label: 'Miasta', route: '/panel/city', icon: 'ic:round-location-city'},
        {label: 'Sponsorzy', route: '/panel/sponsor', icon: 'ic:round-handshake'},
        {label: 'Breaking Change', route: '/panel/breaking-change', icon: 'ic:round-warning-amber'},
        {label: 'GAD', route: '/panel/gad ', icon: 'ic:outline-curtains'},
        {label: 'SAK', route: '/panel/sak ', icon: 'ic:outline-curtains'}
    ];

    export let data: Cities

    city.set(data.cities[0])

    let selectedCityId: number;

    function handleCityChange(event: Event) {
        console.log((event.target as HTMLSelectElement).value);
        const selectedCity = data.cities.find(city => city.id === selectedCityId);
        city.set(selectedCity!);
    }
</script>

<Toast/>

<AppShell slotSidebarLeft="bg-surface-500/5 w-64 p-4">

    <div class="p-6">
        <slot/>
    </div>
    <svelte:fragment slot="sidebarLeft">
        <div class="flex h-full flex-col">
            <nav class="list-nav flex-auto">
                <div class="mt-4 mb-12 text-center">
                    <a href="/panel">
                        <span class="text-2xl font-semibold text-primary-600">Odyseja Umysłu</span>
                    </a>
                </div>
                <select bind:value={selectedCityId} on:change={handleCityChange} class="select mb-10">
                    {#each data.cities as city}
                        <option value={city.id}>{city.name}</option>
                    {/each}
                </select>

                <Navigation menuItems={menuItems}/>
            </nav>
            <div class="flex flex-row justify-between items-center">
                <button type="button" class="btn btn-sm variant-filled-primary w-32 font-semibold" on:click={logout}>
                    Wyloguj
                </button>
                <LightSwitch class=""/>
            </div>
        </div>
    </svelte:fragment>
</AppShell>