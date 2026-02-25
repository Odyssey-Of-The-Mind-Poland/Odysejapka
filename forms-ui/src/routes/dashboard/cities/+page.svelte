<script lang="ts">
    import * as Table from '$lib/components/ui/table/index.js';
    import {Button} from '$lib/components/ui/button/index.js';
    import {Input} from '$lib/components/ui/input/index.js';
    import {createOdysejaQuery, createPostMutation, createDelMutation} from '$lib/queries';
    import IconTrash from '@tabler/icons-svelte/icons/trash';
    import IconPlus from '@tabler/icons-svelte/icons/plus';
    import IconBuilding from '@tabler/icons-svelte/icons/building';
    import {toast} from 'svelte-sonner';
    import {setBreadcrumbs} from '$lib/breadcrumbs';
    import {onMount} from 'svelte';

    onMount(() => {
        setBreadcrumbs([
            {name: 'Dashboard', href: '/dashboard'},
            {name: 'Miasta', href: '/dashboard/cities'},
        ]);
    });

    type City = {
        id: number;
        name: string;
    };

    let newCityName = $state('');

    let citiesQuery = createOdysejaQuery<City[]>({
        queryKey: ['cities'],
        path: '/api/v1/city',
    });

    let addCityMutation = createPostMutation<City, { name: string }>({
        path: '/api/v1/city',
        queryKey: ['cities'],
        onSuccess: () => {
            newCityName = '';
            toast.success('Miasto zostało dodane');
        },
    });

    let deleteCityMutation = createDelMutation<void, { cityId: number }>({
        path: (vars) => `/api/v1/city/${vars.cityId}`,
        queryKey: ['cities'],
        onSuccess: () => {
            toast.success('Miasto zostało usunięte');
        },
    });

    function handleAdd() {
        const trimmed = newCityName.trim();
        if (!trimmed) return;
        addCityMutation.mutate({name: trimmed});
    }

    function handleDelete(city: City) {
        deleteCityMutation.mutate({cityId: city.id});
    }

    function handleKeydown(e: KeyboardEvent) {
        if (e.key === 'Enter') {
            handleAdd();
        }
    }
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
                    {#if citiesQuery.data && citiesQuery.data.length > 0}
                        {citiesQuery.data.length} {citiesQuery.data.length === 1 ? 'miasto' : 'miast'}
                    {:else}
                        Zarządzaj miastami
                    {/if}
                </p>
            </div>
        </div>
    </div>

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

    {#if citiesQuery.isPending}
        <div class="text-muted-foreground text-sm">Ładowanie...</div>
    {:else if citiesQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd podczas ładowania miast</p>
        </div>
    {:else if citiesQuery.data && citiesQuery.data.length > 0}
        <div class="rounded-xl border bg-card shadow-sm overflow-hidden max-w-md">
            <Table.Root>
                <Table.Header>
                    <Table.Row class="bg-muted/40 hover:bg-muted/40">
                        <Table.Head class="font-semibold">Nazwa</Table.Head>
                        <Table.Head class="w-16"></Table.Head>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each citiesQuery.data as city (city.id)}
                        <Table.Row>
                            <Table.Cell class="font-medium">{city.name}</Table.Cell>
                            <Table.Cell class="text-right">
                                <Button
                                        variant="ghost"
                                        size="icon"
                                        onclick={() => handleDelete(city)}
                                        disabled={deleteCityMutation.isPending}
                                >
                                    <IconTrash class="size-4 text-red-500"/>
                                </Button>
                            </Table.Cell>
                        </Table.Row>
                    {/each}
                </Table.Body>
            </Table.Root>
        </div>
    {:else}
        <div class="rounded-lg border border-dashed p-12 text-center max-w-md">
            <IconBuilding class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
            <p class="text-muted-foreground font-medium">Brak miast</p>
            <p class="text-sm text-muted-foreground/70 mt-1">Dodaj pierwsze miasto powyżej.</p>
        </div>
    {/if}
</div>
