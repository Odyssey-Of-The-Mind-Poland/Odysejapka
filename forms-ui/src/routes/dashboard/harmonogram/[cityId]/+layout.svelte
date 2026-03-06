<script lang="ts">
    import {page} from "$app/state";
    import {goto} from "$app/navigation";
    import {createOdysejaQuery} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import IconBuildingArch from "@tabler/icons-svelte/icons/building-arch";
    import IconMessages from "@tabler/icons-svelte/icons/messages";

    type City = {
        id: number;
        name: string;
    };

    let cityId = $derived(Number(page.params.cityId));

    let citiesQuery = $derived(createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    }));

    let cityName = $derived.by(() => {
        if (!citiesQuery.data) return '...';
        return citiesQuery.data.find((c: City) => c.id === cityId)?.name ?? '...';
    });

    const tabs = [
        {id: 'stages', label: 'Sceny', icon: IconBuildingArch},
        {id: 'spontan', label: 'Spontany', icon: IconMessages},
    ];

    let activeTab = $derived.by(() => {
        const path = page.url.pathname;
        const found = tabs.find(t => path.includes(`/${t.id}`));
        return found?.id ?? 'stages';
    });

    $effect(() => {
        setBreadcrumbs([
            {name: 'Harmonogram', href: '/dashboard/harmonogram'},
            {name: cityName, href: `/dashboard/harmonogram/${cityId}/stages`},
        ]);
    });

    $effect(() => {
        const path = page.url.pathname;
        if (path === `/dashboard/harmonogram/${cityId}`) {
            goto(`/dashboard/harmonogram/${cityId}/stages`, {replaceState: true});
        }
    });
</script>

<div class="flex flex-col gap-4 h-full">
    <div class="flex items-center justify-between">
        <div class="flex gap-1 rounded-lg bg-muted p-1 w-fit">
            {#each tabs as tab (tab.id)}
                <button
                        class="flex items-center gap-1.5 rounded-md px-3 py-1.5 text-sm font-medium transition-colors {activeTab === tab.id ? 'bg-background shadow-sm' : 'text-muted-foreground hover:text-foreground'}"
                        onclick={() => goto(`/dashboard/harmonogram/${cityId}/${tab.id}`)}
                >
                    <tab.icon class="size-4"/>
                    {tab.label}
                </button>
            {/each}
        </div>
    </div>

    <slot/>
</div>
