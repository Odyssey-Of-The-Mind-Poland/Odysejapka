<script lang="ts">
    import {page} from "$app/state";
    import {Spinner} from "$lib/components/ui/spinner";
    import {Input} from "$lib/components/ui/input/index.js";
    import {Button} from "$lib/components/ui/button/index.js";
    import * as Select from "$lib/components/ui/select/index.js";
    import {createOdysejaQuery, createPostMutation} from "$lib/queries";
    import {toast} from "svelte-sonner";
    import IconBuildingArch from "@tabler/icons-svelte/icons/building-arch";
    import IconSearch from "@tabler/icons-svelte/icons/search";
    import IconPlus from "@tabler/icons-svelte/icons/plus";
    import type {Team, Stage, TimeSlot, SpontanSlot, SpontanRoom} from "../../types";
    import TeamPool from "../TeamPool.svelte";
    import SlotCell from "../SlotCell.svelte";
    import GenerateTimeSlotsDialog from "../GenerateTimeSlotsDialog.svelte";

    let cityId = $derived(Number(page.params.cityId));

    let teamsQuery = $derived(createOdysejaQuery<Team[]>({
        queryKey: ['harmonogramTeams', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/teams`,
    }));

    let stagesQuery = $derived(createOdysejaQuery<Stage[]>({
        queryKey: ['harmonogramStages', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/stages`,
    }));

    let timeSlotsQuery = $derived(createOdysejaQuery<TimeSlot[]>({
        queryKey: ['harmonogramTimeSlots', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/time-slots`,
    }));

    let spontanSlotsQuery = $derived(createOdysejaQuery<SpontanSlot[]>({
        queryKey: ['harmonogramSpontanSlots', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/spontan-slots`,
    }));

    let spontanRoomsQuery = $derived(createOdysejaQuery<SpontanRoom[]>({
        queryKey: ['harmonogramSpontanRooms', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/spontan-rooms`,
    }));

    let assignMutation = $derived(createPostMutation<void, { slotId: number; teamId: number }>({
        path: (vars) => `/api/v1/harmonogram/${cityId}/time-slots/${vars.slotId}/assign/${vars.teamId}`,
        queryKey: ['harmonogramTimeSlots', String(cityId)],
        onSuccess: () => toast.success('Druzyna przypisana do sceny'),
    }));

    let removeMutation = $derived(createPostMutation<void, { slotId: number }>({
        path: (vars) => `/api/v1/harmonogram/${cityId}/time-slots/${vars.slotId}/unassign`,
        queryKey: ['harmonogramTimeSlots', String(cityId)],
        onSuccess: () => toast.success('Druzyna usunieta ze slotu'),
    }));

    let generateMutation = $derived(createPostMutation<void, { stageId: number; startTime: string; slotDurationMinutes: number; count: number }>({
        path: () => `/api/v1/harmonogram/${cityId}/time-slots/generate`,
        queryKey: ['harmonogramTimeSlots', String(cityId)],
        onSuccess: () => {
            toast.success('Sloty wygenerowane');
            generateDialogOpen = false;
        },
    }));

    function handleDrop(slotId: number, teamId: number) {
        assignMutation.mutate({slotId, teamId});
    }

    function handleRemove(slotId: number) {
        removeMutation.mutate({slotId});
    }

    function handleGenerate(stageId: number, startTime: string, slotDurationMinutes: number, count: number) {
        generateMutation.mutate({stageId, startTime, slotDurationMinutes, count});
    }

    // Selected stage
    let selectedStageId = $state<string>('');
    let sortedStages = $derived(
        (stagesQuery.data ?? []).slice().sort((a, b) => a.number - b.number)
    );

    // Auto-select first stage when data loads
    $effect(() => {
        if (sortedStages.length > 0 && !selectedStageId) {
            selectedStageId = String(sortedStages[0].id);
        }
    });

    let selectedStage = $derived(
        sortedStages.find(s => String(s.id) === selectedStageId)
    );

    // Slots for selected stage only
    let currentSlots = $derived.by(() => {
        if (!timeSlotsQuery.data || !selectedStageId) return [];
        return timeSlotsQuery.data
            .filter(s => s.stageId === Number(selectedStageId))
            .sort((a, b) => a.startTime.localeCompare(b.startTime));
    });

    let filledCount = $derived(currentSlots.filter(s => s.teamId != null).length);

    // Teams not assigned to any stage time slot
    let unassignedTeams = $derived.by(() => {
        if (!teamsQuery.data || !timeSlotsQuery.data) return [];
        const assignedIds = new Set(
            timeSlotsQuery.data.filter(s => s.teamId != null).map(s => s.teamId!)
        );
        return teamsQuery.data.filter(t => !assignedIds.has(t.id));
    });

    // teamId -> spontan info string
    let teamSpontanMap = $derived.by(() => {
        if (!spontanSlotsQuery.data || !spontanRoomsQuery.data) return new Map<number, string>();
        const roomMap = new Map(spontanRoomsQuery.data.map(r => [r.id, r.name]));
        const result = new Map<number, string>();
        for (const slot of spontanSlotsQuery.data) {
            if (slot.teamId != null) {
                const roomName = roomMap.get(slot.roomId) ?? `Pokoj ${slot.roomId}`;
                const time = slot.startTime?.substring(0, 5) ?? '';
                result.set(slot.teamId, `${roomName} ${time}`);
            }
        }
        return result;
    });

    let searchFilter = $state('');
    let generateDialogOpen = $state(false);

    let isLoading = $derived(
        (teamsQuery.isPending && teamsQuery.isFetching) ||
        (stagesQuery.isPending && stagesQuery.isFetching) ||
        (timeSlotsQuery.isPending && timeSlotsQuery.isFetching)
    );
    let hasError = $derived(
        teamsQuery.error || stagesQuery.error || timeSlotsQuery.error
    );
</script>

<div class="flex flex-col gap-4 h-full">
    <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconBuildingArch class="size-5 text-primary"/>
            </div>
            <div>
                <h1 class="text-xl font-semibold tracking-tight">Harmonogram scen</h1>
                <p class="text-sm text-muted-foreground">Przeciagnij druzyny na sloty czasowe scen</p>
            </div>
        </div>
        {#if !isLoading && !hasError && sortedStages.length > 0}
            <Button variant="outline" size="sm" onclick={() => generateDialogOpen = true}>
                <IconPlus class="size-4 mr-1.5"/>
                Generuj sloty
            </Button>
        {/if}
    </div>

    {#if isLoading}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ladowanie harmonogramu...</p>
        </div>
    {:else if hasError}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Blad ladowania danych</p>
        </div>
    {:else}
        <div class="grid grid-cols-[280px_1fr] gap-4 h-full min-h-0">
            <!-- Team pool (left sidebar) -->
            <div class="flex flex-col gap-3 border rounded-lg p-3 bg-muted/20 overflow-hidden">
                <div>
                    <h2 class="font-semibold text-sm mb-2">
                        Druzyny ({unassignedTeams.length})
                    </h2>
                    <div class="relative">
                        <IconSearch class="absolute left-2.5 top-1/2 -translate-y-1/2 size-4 text-muted-foreground"/>
                        <Input
                            bind:value={searchFilter}
                            placeholder="Szukaj druzyny..."
                            class="pl-8 h-8 text-sm"
                        />
                    </div>
                </div>
                <div class="overflow-y-auto flex-1 -mr-1 pr-1">
                    <TeamPool teams={unassignedTeams} filter={searchFilter}/>
                </div>
            </div>

            <!-- Stage view (main area) -->
            <div class="flex flex-col gap-3 overflow-hidden">
                {#if sortedStages.length === 0}
                    <div class="rounded-lg border border-dashed p-12 text-center">
                        <IconBuildingArch class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
                        <p class="text-muted-foreground font-medium">Brak scen</p>
                        <p class="text-sm text-muted-foreground/70 mt-1">Dodaj sceny aby rozpoczac planowanie.</p>
                    </div>
                {:else}
                    <!-- Stage selector -->
                    <div class="flex items-center gap-3">
                        <Select.Root type="single" bind:value={selectedStageId}>
                            <Select.Trigger class="w-64">
                                {selectedStage ? (selectedStage.name || `Scena ${selectedStage.number}`) : 'Wybierz scene...'}
                            </Select.Trigger>
                            <Select.Content>
                                {#each sortedStages as stage (stage.id)}
                                    <Select.Item value={String(stage.id)}>
                                        {stage.name || `Scena ${stage.number}`}
                                    </Select.Item>
                                {/each}
                            </Select.Content>
                        </Select.Root>
                        <span class="text-sm text-muted-foreground">
                            {filledCount}/{currentSlots.length} slotow zajętych
                        </span>
                    </div>

                    <!-- Slots list -->
                    <div class="overflow-y-auto flex-1">
                        {#if currentSlots.length === 0}
                            <div class="rounded-lg border border-dashed p-8 text-center">
                                <p class="text-sm text-muted-foreground">Brak slotow dla tej sceny</p>
                                <p class="text-xs text-muted-foreground/70 mt-1">Uzyj "Generuj sloty" aby dodac sloty czasowe.</p>
                            </div>
                        {:else}
                            <div class="flex flex-col gap-1" role="list">
                                {#each currentSlots as slot (slot.id)}
                                    <SlotCell
                                        startTime={slot.startTime}
                                        endTime={slot.endTime}
                                        teamName={slot.teamName}
                                        slotId={slot.id}
                                        ondrop={handleDrop}
                                        onremove={handleRemove}
                                        spontanInfo={slot.teamId != null ? (teamSpontanMap.get(slot.teamId) ?? null) : null}
                                    />
                                {/each}
                            </div>
                        {/if}
                    </div>
                {/if}
            </div>
        </div>
    {/if}
</div>

<GenerateTimeSlotsDialog
    bind:open={generateDialogOpen}
    stages={stagesQuery.data ?? []}
    saving={generateMutation.isPending}
    preselectedStageId={selectedStageId}
    onsave={handleGenerate}
/>
