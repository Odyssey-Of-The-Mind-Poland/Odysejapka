<script lang="ts">
    import {page} from "$app/state";
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery, createPostMutation, createPutMutation, createDelMutation} from "$lib/queries";
    import IconInfoCircle from "@tabler/icons-svelte/icons/info-circle";
    import {toast} from "svelte-sonner";
    import {apiFetch} from "$lib/api";
    import {getQueryClientContext} from "@tanstack/svelte-query";
    import type {Info, InfoCategory, InfoResponse} from "./types";
    import InfoCategoryCard from "./InfoCategoryCard.svelte";
    import InfoEditDialog from "./InfoEditDialog.svelte";
    import InfoForm from "./InfoForm.svelte";
    import {Button} from "$lib/components/ui/button/index.js";
    import * as Card from "$lib/components/ui/card/index.js";

    let cityId = $derived(Number(page.params.cityId));
    const queryClient = getQueryClientContext();

    let infoQuery = $derived(createOdysejaQuery<InfoResponse>({
        queryKey: ['lappkaInfo', String(cityId)],
        path: `/api/info/map?cityId=${cityId}`,
    }));

    type City = { id: number; name: string; level?: 'FINAL' | 'REGIONAL' };
    let citiesQuery = $derived(createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    }));
    // Only the finals have many infos grouped in categories; a regional city has just one.
    let isRegional = $derived(citiesQuery.data?.find(c => c.id === cityId)?.level === 'REGIONAL');

    let categories = $derived(infoQuery.data?.categories ?? []);
    let infos = $derived(infoQuery.data?.infos ?? []);

    const bySortNumberDesc = (a: Info, b: Info) => b.sortNumber - a.sortNumber;

    // Per-category local items for dnd
    let categoryItems = $state<Record<number, Info[]>>({});

    $effect(() => {
        const next: Record<number, Info[]> = {};
        for (const cat of categories) {
            next[cat.id] = infos
                .filter(i => i.category === cat.id)
                .sort(bySortNumberDesc);
        }
        categoryItems = next;
    });

    function handleSort(catId: number, e: CustomEvent) {
        categoryItems[catId] = e.detail.items;
    }

    async function handleFinalize(catId: number, e: CustomEvent) {
        const items: Info[] = e.detail.items;
        categoryItems[catId] = items;

        const original = infos
            .filter(i => i.category === catId)
            .sort(bySortNumberDesc);
        const changed = items.some((item, idx) => original[idx]?.id !== item.id);
        if (!changed) return;

        try {
            for (let i = 0; i < items.length; i++) {
                const sortNumber = items.length - 1 - i;
                if (items[i].sortNumber !== sortNumber) {
                    await apiFetch('/api/info', {
                        method: 'PUT',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({...items[i], sortNumber}),
                    });
                }
            }
            await queryClient.invalidateQueries({queryKey: ['lappkaInfo', String(cityId)]});
            toast.success('Kolejność zapisana');
        } catch {
            toast.error('Błąd zapisywania kolejności');
        }
    }

    // Dialog state
    let dialogOpen = $state(false);
    let editingInfo = $state<Info | null>(null);
    let selectedCategory = $state<InfoCategory | null>(null);
    let formName = $state('');
    let formText = $state('');
    let formSort = $state(0);
    let formIcon = $state<string | null>(null);
    let formColor = $state<string | null>(null);

    let createMutation = createPostMutation<Info, Info>({
        path: '/api/info',
        queryKey: ['lappkaInfo'],
        onSuccess: () => {
            dialogOpen = false;
            toast.success('Informacja dodana');
        },
    });

    let updateMutation = createPutMutation<Info, Info>({
        path: '/api/info',
        queryKey: ['lappkaInfo'],
        onSuccess: () => {
            dialogOpen = false;
            toast.success('Informacja zapisana');
        },
    });

    let deleteMutation = createDelMutation<void, { infoId: number }>({
        path: (vars) => `/api/info/${vars.infoId}`,
        queryKey: ['lappkaInfo'],
        onSuccess: () => toast.success('Informacja usunięta'),
    });

    function openCreate(category: InfoCategory) {
        const catInfos = categoryItems[category.id] ?? [];
        // A new info goes to the end of the list, i.e. below the lowest sortNumber.
        const minSort = catInfos.length > 0 ? Math.min(...catInfos.map(i => i.sortNumber)) : 1;
        editingInfo = null;
        formName = '';
        formText = '';
        formSort = minSort - 1;
        formIcon = null;
        formColor = null;
        selectedCategory = category;
        dialogOpen = true;
    }

    function openEdit(info: Info) {
        editingInfo = info;
        formName = info.infoName;
        formText = info.infoText;
        formSort = info.sortNumber;
        formIcon = info.icon ?? null;
        formColor = info.color ?? null;
        selectedCategory = categories.find(c => c.id === info.category) ?? null;
        dialogOpen = true;
    }

    // The single info of a regional city is edited in place, not in a dialog.
    let regionalInfo = $derived(
        isRegional ? ([...infos].sort(bySortNumberDesc)[0] ?? null) : null
    );
    let loadedRegionalKey = $state<string | null>(null);

    $effect(() => {
        if (!isRegional || infoQuery.isPending) return;
        const key = `${cityId}:${regionalInfo?.id ?? 'new'}`;
        if (key === loadedRegionalKey) return;
        loadedRegionalKey = key;
        formName = regionalInfo?.infoName ?? 'Informacje';
        formText = regionalInfo?.infoText ?? '';
        formIcon = regionalInfo?.icon ?? null;
        formColor = regionalInfo?.color ?? null;
    });

    function saveRegional() {
        if (!formName.trim()) return;
        // The category is not shown for a regional city, but every info belongs to one.
        const category = categories.find(c => c.id === regionalInfo?.category) ?? categories[0];
        const payload: Info = {
            id: regionalInfo?.id ?? 0,
            infoName: formName.trim(),
            infoText: formText,
            city: cityId,
            category: category?.id ?? 0,
            sortNumber: regionalInfo?.sortNumber ?? 0,
            categoryName: category?.name ?? 'Dla wszystkich',
            icon: formIcon,
            color: formColor,
        };
        if (regionalInfo) {
            updateMutation.mutate(payload);
        } else {
            createMutation.mutate(payload);
        }
    }

    function handleSave() {
        if (!formName.trim() || !selectedCategory) return;
        const payload: Info = {
            id: editingInfo?.id ?? 0,
            infoName: formName.trim(),
            infoText: formText,
            city: cityId,
            category: selectedCategory.id,
            sortNumber: formSort,
            categoryName: selectedCategory.name,
            icon: formIcon,
            color: formColor,
        };
        if (editingInfo) {
            updateMutation.mutate(payload);
        } else {
            createMutation.mutate(payload);
        }
    }
</script>

<div class="flex flex-col gap-6">
    {#if infoQuery.isPending || citiesQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie informacji...</p>
        </div>
    {:else if infoQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd ładowania</p>
        </div>
    {:else if isRegional}
        <Card.Root>
            <Card.Header>
                <Card.Title>Informacja o mieście</Card.Title>
                <Card.Description>
                    Miasto regionalne ma jedną informację, którą aplikacja pokazuje od razu.
                    {#if infos.length > 1}
                        Ma ich {infos.length}, edytujesz pierwszą.
                    {/if}
                </Card.Description>
            </Card.Header>
            <Card.Content>
                <InfoForm
                        bind:name={formName}
                        bind:text={formText}
                        bind:icon={formIcon}
                        bind:color={formColor}
                        inheritColorLabel="Domyślny"
                        editorKey={loadedRegionalKey ?? 'new'}
                />
            </Card.Content>
            <Card.Footer class="justify-end">
                <Button
                        onclick={saveRegional}
                        disabled={!formName.trim() || createMutation.isPending || updateMutation.isPending}
                >
                    Zapisz
                </Button>
            </Card.Footer>
        </Card.Root>
    {:else if categories.length === 0}
        <div class="rounded-lg border border-dashed p-12 text-center">
            <IconInfoCircle class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
            <p class="text-muted-foreground font-medium">Brak kategorii informacji</p>
        </div>
    {:else}
        {#each categories as category (category.id)}
            {#if categoryItems[category.id]}
                <InfoCategoryCard
                    {category}
                    bind:items={categoryItems[category.id]}
                    deleting={deleteMutation.isPending}
                    oncreate={() => openCreate(category)}
                    onedit={openEdit}
                    ondelete={(info) => deleteMutation.mutate({infoId: info.id})}
                    onsort={(e) => handleSort(category.id, e)}
                    onfinalize={(e) => handleFinalize(category.id, e)}
                />
            {/if}
        {/each}
    {/if}
</div>

<InfoEditDialog
    bind:open={dialogOpen}
    {editingInfo}
    category={selectedCategory}
    bind:name={formName}
    bind:text={formText}
    bind:icon={formIcon}
    bind:color={formColor}
    saving={createMutation.isPending || updateMutation.isPending}
    onsave={handleSave}
/>
