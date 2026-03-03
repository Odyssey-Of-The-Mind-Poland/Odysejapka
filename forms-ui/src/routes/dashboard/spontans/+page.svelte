<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {onMount} from "svelte";
    import {goto} from "$app/navigation";
    import * as Card from "$lib/components/ui/card/index.js";
    import IconSparkles from "@tabler/icons-svelte/icons/sparkles";
    import IconBuilding from "@tabler/icons-svelte/icons/building";
    import IconChevronRight from "@tabler/icons-svelte/icons/chevron-right";

    type City = {
        id: number;
        name: string;
    };

    let citiesQuery = $derived(createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    }));

    let cities = $derived.by(() => {
        if (!citiesQuery.data) return [];
        return [...citiesQuery.data].sort((a, b) => a.name.localeCompare(b.name));
    });

    onMount(() => {
        setBreadcrumbs([
            {name: 'Spontany', href: '/dashboard/spontans'},
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex flex-col gap-1">
        <div class="flex items-center gap-3">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconSparkles class="size-5 text-primary"/>
            </div>
            <div>
                <h1 class="text-2xl font-semibold tracking-tight">Spontany</h1>
                <p class="text-sm text-muted-foreground">
                    {#if cities.length > 0}
                        Wybierz miasto
                    {:else}
                        Brak miast
                    {/if}
                </p>
            </div>
        </div>
    </div>

    {#if citiesQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie...</p>
        </div>
    {:else if citiesQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd podczas ładowania</p>
            <p class="text-sm text-muted-foreground mt-1">{String(citiesQuery.error)}</p>
        </div>
    {:else if cities.length === 0}
        <div class="rounded-lg border border-dashed p-12 text-center">
            <IconBuilding class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
            <p class="text-muted-foreground font-medium">Brak miast</p>
            <p class="text-sm text-muted-foreground/70 mt-1">Miasta pojawią się po ich zarejestrowaniu.</p>
        </div>
    {:else}
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {#each cities as city (city.id)}
                <button
                        class="text-left w-full"
                        onclick={() => goto(`/dashboard/spontans/city/${city.id}`)}
                >
                    <Card.Root
                            class="cursor-pointer transition-all hover:shadow-md hover:border-primary/30"
                    >
                        <Card.Header>
                            <div class="flex items-center justify-between">
                                <div class="flex items-center gap-3">
                                    <div class="flex items-center justify-center size-9 rounded-md bg-muted">
                                        <IconBuilding class="size-4 text-muted-foreground"/>
                                    </div>
                                    <div>
                                        <Card.Title class="text-base">{city.name}</Card.Title>
                                    </div>
                                </div>
                                <IconChevronRight class="size-5 text-muted-foreground"/>
                            </div>
                        </Card.Header>
                    </Card.Root>
                </button>
            {/each}
        </div>
    {/if}
</div>
