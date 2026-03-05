<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery, createPostMutation, createDelMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {onMount} from "svelte";
    import * as Card from "$lib/components/ui/card/index.js";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Input} from "$lib/components/ui/input/index.js";
    import RequirePermission from "$lib/components/RequirePermission.svelte";
    import IconBuilding from "@tabler/icons-svelte/icons/building";
    import IconPlus from "@tabler/icons-svelte/icons/plus";
    import IconTrash from "@tabler/icons-svelte/icons/trash";
    import {toast} from "svelte-sonner";

    type City = {
        id: number;
        name: string;
    };

    let newCityName = $state('');

    let citiesQuery = createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    });

    let cities = $derived.by(() => {
        if (!citiesQuery.data) return [];
        return [...citiesQuery.data].sort((a, b) => a.name.localeCompare(b.name));
    });

    let addCityMutation = createPostMutation<City, { name: string }>({
        path: '/api/v1/city',
        queryKey: ['dashboardCities'],
        onSuccess: () => {
            newCityName = '';
            toast.success('Miasto zostało dodane');
        },
    });

    let deleteCityMutation = createDelMutation<void, { cityId: number }>({
        path: (vars) => `/api/v1/city/${vars.cityId}`,
        queryKey: ['dashboardCities'],
        onSuccess: () => {
            toast.success('Miasto zostało usunięte');
        },
    });

    function handleAdd() {
        const trimmed = newCityName.trim();
        if (!trimmed) return;
        addCityMutation.mutate({name: trimmed});
    }

    function handleKeydown(e: KeyboardEvent) {
        if (e.key === 'Enter') handleAdd();
    }

    onMount(() => {
        setBreadcrumbs([
            {name: 'Miasta', href: '/dashboard/cities'},
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex flex-col gap-1">
        <div class="flex items-center gap-3">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconBuilding class="size-5 text-primary"/>
            </div>
            <div>
                <h1 class="text-2xl font-semibold tracking-tight">Miasta</h1>
                <p class="text-sm text-muted-foreground">
                    Zarządzaj miastami w systemie
                </p>
            </div>
        </div>
    </div>

    <RequirePermission role="ADMINISTRATOR">
        <div class="flex items-center gap-2 max-w-md">
            <Input
                    bind:value={newCityName}
                    placeholder="Nazwa miasta..."
                    onkeydown={handleKeydown}
            />
            <Button
                    onclick={handleAdd}
                    disabled={!newCityName.trim() || addCityMutation.isPending}
            >
                <IconPlus class="size-4 mr-1"/>
                Dodaj
            </Button>
        </div>
    </RequirePermission>

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
                <Card.Root>
                    <Card.Header>
                        <div class="flex items-center justify-between">
                            <div class="flex items-center gap-3">
                                <div class="flex items-center justify-center size-9 rounded-md bg-muted">
                                    <IconBuilding class="size-4 text-muted-foreground"/>
                                </div>
                                <Card.Title class="text-base">{city.name}</Card.Title>
                            </div>
                            <RequirePermission role="ADMINISTRATOR">
                                <Button
                                        variant="ghost"
                                        size="icon"
                                        class="size-8 text-muted-foreground hover:text-red-500"
                                        onclick={() => deleteCityMutation.mutate({cityId: city.id})}
                                        disabled={deleteCityMutation.isPending}
                                >
                                    <IconTrash class="size-4"/>
                                </Button>
                            </RequirePermission>
                        </div>
                    </Card.Header>
                </Card.Root>
            {/each}
        </div>
    {/if}
</div>
