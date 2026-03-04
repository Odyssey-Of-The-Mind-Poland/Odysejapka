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

    let cityId = $derived(Number(page.params.cityId));
    const queryClient = getQueryClientContext();

    let infoQuery = $derived(createOdysejaQuery<InfoResponse>({
        queryKey: ['lappkaInfo', String(cityId)],
        path: `/api/info/v2?cityId=${cityId}`,
    }));

    let categories = $derived(infoQuery.data?.categories ?? []);
    let infos = $derived(infoQuery.data?.infos ?? []);

    // Per-category local items for dnd
    let categoryItems = $state<Record<number, Info[]>>({});

    $effect(() => {
        const next: Record<number, Info[]> = {};
        for (const cat of categories) {
            next[cat.id] = infos
                .filter(i => i.categoryName === cat.name)
                .sort((a, b) => a.sortNumber - b.sortNumber);
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
            .sort((a, b) => a.sortNumber - b.sortNumber);
        const changed = items.some((item, idx) => original[idx]?.id !== item.id);
        if (!changed) return;

        try {
            for (let i = 0; i < items.length; i++) {
                if (items[i].sortNumber !== i) {
                    await apiFetch('/api/info', {
                        method: 'PUT',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({...items[i], sortNumber: i}),
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
        const maxSort = catInfos.length > 0 ? Math.max(...catInfos.map(i => i.sortNumber)) : -1;
        editingInfo = null;
        formName = '';
        formText = '';
        formSort = maxSort + 1;
        selectedCategory = category;
        dialogOpen = true;
    }

    function openEdit(info: Info) {
        editingInfo = info;
        formName = info.infoName;
        formText = info.infoText;
        formSort = info.sortNumber;
        selectedCategory = categories.find(c => c.id === info.category) ?? null;
        dialogOpen = true;
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
            icon: editingInfo?.icon,
            color: editingInfo?.color,
        };
        if (editingInfo) {
            updateMutation.mutate(payload);
        } else {
            createMutation.mutate(payload);
        }
    }
</script>

<div class="flex flex-col gap-6">
    {#if infoQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie informacji...</p>
        </div>
    {:else if infoQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd ładowania</p>
        </div>
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
    saving={createMutation.isPending || updateMutation.isPending}
    onsave={handleSave}
/>
