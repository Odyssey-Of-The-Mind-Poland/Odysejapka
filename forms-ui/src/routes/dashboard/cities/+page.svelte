<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery, createPostMutation, createDelMutation, createPutMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {onMount} from "svelte";
    import * as Card from "$lib/components/ui/card/index.js";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Input} from "$lib/components/ui/input/index.js";
    import RequirePermission from "$lib/components/RequirePermission.svelte";
    import IconBuilding from "@tabler/icons-svelte/icons/building";
    import IconPlus from "@tabler/icons-svelte/icons/plus";
    import IconTrash from "@tabler/icons-svelte/icons/trash";
    import IconEdit from "@tabler/icons-svelte/icons/edit";
    import {toast} from "svelte-sonner";
    import * as Select from '$lib/components/ui/select/index.js';

    type City = {
        id: number;
        name: string;
        level: string;
    };

    let newCityName = $state('');
    let newCityLevel = $state('REGIONAL');
    let currentEditingId: number | null = $state(null);
    let updatedName = $state('');
    let updatedLevel = $state('');

    let citiesQuery = createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    });

    let cities = $derived.by(() => {
        if (!citiesQuery.data) return [];
        return [...citiesQuery.data].sort((a, b) => a.name.localeCompare(b.name));
    });

    let addCityMutation = createPostMutation<City, { name: string, level: string }>({
        path: '/api/v1/city',
        queryKey: ['dashboardCities'],
        onSuccess: () => {
            newCityName = '';
            newCityLevel = 'REGIONAL';
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

    let updateCityMutation = createPutMutation<City, { id: number, name: string, level: string }>({
        path: '/api/v1/city',
        queryKey: ['dashboardCities'],
        onSuccess: () => {
            toast.success('Miasto zostało zaktualizowane');
        },
    })

    function handleAdd() {
        const trimmed = newCityName.trim();
        if (!trimmed) return;
        addCityMutation.mutate({name: trimmed, level: newCityLevel});
    }

    function handleSaveEdits(city: City) {
        city.name = updatedName;
        city.level = updatedLevel;

        updateCityMutation.mutate({
            id: city.id,
            name: city.name,
            level: city.level
        });

        currentEditingId = null;
        updatedName = '';
        updatedLevel = '';
    }

    function handleCancelEdits() {
        currentEditingId = null;
        updatedName = '';
        updatedLevel = '';
    }

    function handleKeydown(e: KeyboardEvent) {
        if (e.key === 'Enter') handleAdd();
    }

    function mapLevel(level: string) {
        return level === 'REGIONAL'
            ? 'Finał Regionalny'
            : 'Finał Ogólnopolski'
    }

    function startEditing(city: City) {
        updatedName = city.name;
        updatedLevel = city.level;
        currentEditingId = city.id;
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
        <div class="flex items-center gap-2 max-w-lg">
            <Input
                    bind:value={newCityName}
                    placeholder="Nazwa miasta..."
                    onkeydown={handleKeydown}
            />
            <div class="flex items-center gap-2">
                <Select.Root type="single" bind:value={newCityLevel}>
                    <Select.Trigger class="w-45">
                        {mapLevel(newCityLevel)}
                    </Select.Trigger>
                    <Select.Content>
                        <Select.Item value='REGIONAL'>
                            {mapLevel('REGIONAL')}
                        </Select.Item>
                        <Select.Item value='FINAL'>
                            {mapLevel('FINAL')}
                        </Select.Item>
                    </Select.Content>
                </Select.Root>
            </div>
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
                                {#if currentEditingId === city.id}
                                    <div class="flex flex-col gap-1">
                                        <Input
                                                class="font-base"
                                                bind:value={updatedName}
                                                placeholder="Wprowadź nazwę miasta"
                                        />
                                        <Select.Root type="single" bind:value={updatedLevel}>
                                            <Select.Trigger class="px-3 font-base">
                                                {mapLevel(updatedLevel)}
                                            </Select.Trigger>
                                            <Select.Content>
                                                <Select.Item value="REGIONAL">{mapLevel("REGIONAL")}</Select.Item>
                                                <Select.Item value="FINAL">{mapLevel("FINAL")}</Select.Item>
                                            </Select.Content>
                                        </Select.Root>
                                    </div>
                                {:else}
                                    <div class="flex items-center justify-center size-9 rounded-md bg-muted">
                                        <IconBuilding class="size-4 text-muted-foreground"/>
                                    </div>
                                    <div class="flex-col gap 1">
                                        <Card.Title class="text-base">{city.name}</Card.Title>
                                        <Card.Content
                                                class="px-0 text-sm text-muted-foreground">{mapLevel(city.level)}
                                        </Card.Content>
                                    </div>
                                {/if}
                            </div>
                            <RequirePermission role="ADMINISTRATOR">
                                <div class="flex items-center gap-1">
                                    {#if currentEditingId === city.id}
                                        <Button
                                                variant="ghost"
                                                size="icon"
                                                class="text-white bg-primary hover:bg-primary/90 hover:text-white px-8"
                                                onclick={() => handleSaveEdits(city)}
                                                disabled={updateCityMutation.isPending}
                                        >
                                            Zapisz
                                        </Button>
                                        <Button
                                                variant="ghost"
                                                size="icon"
                                                class="text-muted-foreground hover:text-red-500 px-8"
                                                onclick={handleCancelEdits}
                                        >
                                            Anuluj
                                        </Button>
                                    {:else}
                                        <Button
                                                variant="ghost"
                                                size="icon"
                                                class="size-8 text-muted-foreground hover:ext-primary"
                                                onclick={() => (startEditing(city))}
                                        >
                                        <IconEdit class="size-4" />
                                        </Button>
                                        <Button
                                                variant="ghost"
                                                size="icon"
                                                class="size-8 text-muted-foreground hover:text-red-500"
                                                onclick={() => deleteCityMutation.mutate({ cityId: city.id })}
                                                disabled={deleteCityMutation.isPending}
                                        >
                                            <IconTrash class="size-4" />
                                        </Button>
                                    {/if}
                                </div>
                            </RequirePermission>
                        </div>
                    </Card.Header>
                </Card.Root>
            {/each}
        </div>
    {/if}
</div>
