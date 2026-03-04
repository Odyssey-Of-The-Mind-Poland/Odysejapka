<script lang="ts">
    import {page} from "$app/state";
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Input} from "$lib/components/ui/input/index.js";
    import * as Card from "$lib/components/ui/card/index.js";
    import IconBuildingArch from "@tabler/icons-svelte/icons/building-arch";
    import IconDeviceFloppy from "@tabler/icons-svelte/icons/device-floppy";
    import IconX from "@tabler/icons-svelte/icons/x";
    import {toast} from "svelte-sonner";

    type Stage = {
        id: number;
        number: number;
        city: number;
        name: string;
    };

    let cityId = $derived(Number(page.params.cityId));

    let stagesQuery = $derived(createOdysejaQuery<Stage[]>({
        queryKey: ['lappkaStages', String(cityId)],
        path: `/api/stage?cityId=${cityId}`,
    }));

    let editedNames = $state<Record<number, string>>({});
    let isEditing = $state(false);

    let updateMutation = $derived(createPutMutation<void, Stage[]>({
        path: '/api/stage',
        queryKey: ['lappkaStages', String(cityId)],
        onSuccess: () => {
            isEditing = false;
            editedNames = {};
            toast.success('Sceny zapisane');
        },
    }));

    function startEdit() {
        if (!stagesQuery.data) return;
        editedNames = {};
        for (const stage of stagesQuery.data) {
            editedNames[stage.id] = stage.name;
        }
        isEditing = true;
    }

    function cancelEdit() {
        isEditing = false;
        editedNames = {};
    }

    function handleSave() {
        if (!stagesQuery.data) return;
        const updated = stagesQuery.data.map(s => ({
            ...s,
            name: editedNames[s.id] ?? s.name,
        }));
        updateMutation.mutate(updated);
    }

    let hasChanges = $derived.by(() => {
        if (!stagesQuery.data || !isEditing) return false;
        return stagesQuery.data.some(s => editedNames[s.id] !== s.name);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconBuildingArch class="size-5 text-primary"/>
            </div>
            <div>
                <h1 class="text-xl font-semibold tracking-tight">Sceny</h1>
                <p class="text-sm text-muted-foreground">
                    {#if stagesQuery.data}
                        {stagesQuery.data.length} {stagesQuery.data.length === 1 ? 'scena' : 'scen'}
                    {:else}
                        Zarządzaj nazwami scen
                    {/if}
                </p>
            </div>
        </div>
        {#if !isEditing}
            <Button variant="outline" onclick={startEdit} disabled={!stagesQuery.data || stagesQuery.data.length === 0}>
                Edytuj
            </Button>
        {/if}
    </div>

    {#if stagesQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie scen...</p>
        </div>
    {:else if stagesQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd ładowania</p>
        </div>
    {:else if stagesQuery.data && stagesQuery.data.length === 0}
        <div class="rounded-lg border border-dashed p-12 text-center">
            <IconBuildingArch class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
            <p class="text-muted-foreground font-medium">Brak scen</p>
            <p class="text-sm text-muted-foreground/70 mt-1">Sceny pojawią się po imporcie harmonogramu.</p>
        </div>
    {:else if stagesQuery.data}
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {#each stagesQuery.data.sort((a, b) => a.number - b.number) as stage (stage.id)}
                <Card.Root class={isEditing ? 'border-primary/30' : ''}>
                    <Card.Header class="pb-2">
                        <Card.Description>Scena nr {stage.number}</Card.Description>
                    </Card.Header>
                    <Card.Content>
                        {#if isEditing}
                            <Input
                                    bind:value={editedNames[stage.id]}
                                    placeholder="Nazwa sceny..."
                            />
                        {:else}
                            <p class="font-medium">{stage.name || '—'}</p>
                        {/if}
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>

        {#if isEditing}
            <div class="flex gap-2">
                <Button onclick={handleSave} disabled={!hasChanges || updateMutation.isPending}>
                    <IconDeviceFloppy class="size-4 mr-1"/>
                    Zapisz
                </Button>
                <Button variant="outline" onclick={cancelEdit} disabled={updateMutation.isPending}>
                    <IconX class="size-4 mr-1"/>
                    Anuluj
                </Button>
            </div>
        {/if}
    {/if}
</div>
