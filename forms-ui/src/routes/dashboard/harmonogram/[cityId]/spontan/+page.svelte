<script lang="ts">
    import {page} from "$app/state";
    import {Spinner} from "$lib/components/ui/spinner";
    import {Input} from "$lib/components/ui/input/index.js";
    import {Button} from "$lib/components/ui/button/index.js";
    import * as Select from "$lib/components/ui/select/index.js";
    import {createOdysejaQuery, createPostMutation} from "$lib/queries";
    import {toast} from "svelte-sonner";
    import IconMessages from "@tabler/icons-svelte/icons/messages";
    import IconSearch from "@tabler/icons-svelte/icons/search";
    import IconPlus from "@tabler/icons-svelte/icons/plus";
    import IconClock from "@tabler/icons-svelte/icons/clock";
    import type {Team, SpontanRoom, SpontanSlot, TimeSlot, Stage} from "../../types";
    import TeamPool from "../TeamPool.svelte";
    import SlotCell from "../SlotCell.svelte";
    import AddSpontanRoomDialog from "../AddSpontanRoomDialog.svelte";
    import GenerateSpontanSlotsDialog from "../GenerateSpontanSlotsDialog.svelte";

    let cityId = $derived(Number(page.params.cityId));

    let teamsQuery = $derived(createOdysejaQuery<Team[]>({
        queryKey: ['harmonogramTeams', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/teams`,
    }));

    let roomsQuery = $derived(createOdysejaQuery<SpontanRoom[]>({
        queryKey: ['harmonogramSpontanRooms', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/spontan-rooms`,
    }));

    let slotsQuery = $derived(createOdysejaQuery<SpontanSlot[]>({
        queryKey: ['harmonogramSpontanSlots', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/spontan-slots`,
    }));

    // Also fetch stage time slots to show stage time next to team name
    let timeSlotsQuery = $derived(createOdysejaQuery<TimeSlot[]>({
        queryKey: ['harmonogramTimeSlots', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/time-slots`,
    }));

    let stagesQuery = $derived(createOdysejaQuery<Stage[]>({
        queryKey: ['harmonogramStages', String(cityId)],
        path: `/api/v1/harmonogram/${cityId}/stages`,
    }));

    let assignMutation = $derived(createPostMutation<void, { slotId: number; teamId: number }>({
        path: (vars) => `/api/v1/harmonogram/${cityId}/spontan-slots/${vars.slotId}/assign/${vars.teamId}`,
        queryKey: ['harmonogramSpontanSlots', String(cityId)],
        onSuccess: () => toast.success('Druzyna przypisana do pokoju spontan'),
    }));

    let removeMutation = $derived(createPostMutation<void, { slotId: number }>({
        path: (vars) => `/api/v1/harmonogram/${cityId}/spontan-slots/${vars.slotId}/unassign`,
        queryKey: ['harmonogramSpontanSlots', String(cityId)],
        onSuccess: () => toast.success('Druzyna usunieta ze slotu spontan'),
    }));

    let createRoomMutation = $derived(createPostMutation<void, { name: string }>({
        path: () => `/api/v1/harmonogram/${cityId}/spontan-rooms`,
        queryKey: ['harmonogramSpontanRooms', String(cityId)],
        onSuccess: () => {
            toast.success('Pokoj dodany');
            addRoomDialogOpen = false;
        },
    }));

    let generateMutation = $derived(createPostMutation<void, { roomId: number; startTime: string; slotDurationMinutes: number; count: number }>({
        path: () => `/api/v1/harmonogram/${cityId}/spontan-slots/generate`,
        queryKey: ['harmonogramSpontanSlots', String(cityId)],
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

    function handleCreateRoom(name: string) {
        createRoomMutation.mutate({name});
    }

    function handleGenerateSlots(roomId: number, startTime: string, slotDurationMinutes: number, count: number) {
        generateMutation.mutate({roomId, startTime, slotDurationMinutes, count});
    }

    // Selected room
    let selectedRoomId = $state<string>('');
    let rooms = $derived(roomsQuery.data ?? []);

    $effect(() => {
        if (rooms.length > 0 && !selectedRoomId) {
            selectedRoomId = String(rooms[0].id);
        }
    });

    let selectedRoom = $derived(rooms.find(r => String(r.id) === selectedRoomId));

    // Slots for selected room only
    let currentSlots = $derived.by(() => {
        if (!slotsQuery.data || !selectedRoomId) return [];
        return slotsQuery.data
            .filter(s => s.roomId === Number(selectedRoomId))
            .sort((a, b) => a.startTime.localeCompare(b.startTime));
    });

    let filledCount = $derived(currentSlots.filter(s => s.teamId != null).length);

    // Teams not assigned to any spontan slot
    let unassignedTeams = $derived.by(() => {
        if (!teamsQuery.data || !slotsQuery.data) return [];
        const assignedIds = new Set(
            slotsQuery.data.filter(s => s.teamId != null).map(s => s.teamId!)
        );
        return teamsQuery.data.filter(t => !assignedIds.has(t.id));
    });

    // teamId -> stage time info for display on the right side
    let teamStageMap = $derived.by(() => {
        if (!timeSlotsQuery.data || !stagesQuery.data) return new Map<number, string>();
        const stageMap = new Map(stagesQuery.data.map(s => [s.id, s.name || `Scena ${s.number}`]));
        const result = new Map<number, string>();
        for (const slot of timeSlotsQuery.data) {
            if (slot.teamId != null) {
                const stageName = stageMap.get(slot.stageId) ?? `Scena`;
                const time = slot.startTime?.substring(0, 5) ?? '';
                result.set(slot.teamId, `${stageName} ${time}`);
            }
        }
        return result;
    });

    let searchFilter = $state('');
    let addRoomDialogOpen = $state(false);
    let generateDialogOpen = $state(false);

    let isLoading = $derived(
        (teamsQuery.isPending && teamsQuery.isFetching) ||
        (roomsQuery.isPending && roomsQuery.isFetching) ||
        (slotsQuery.isPending && slotsQuery.isFetching)
    );
    let hasError = $derived(
        teamsQuery.error || roomsQuery.error || slotsQuery.error
    );
</script>

<div class="flex flex-col gap-4 h-full">
    <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconMessages class="size-5 text-primary"/>
            </div>
            <div>
                <h1 class="text-xl font-semibold tracking-tight">Harmonogram spontanow</h1>
                <p class="text-sm text-muted-foreground">Przeciagnij druzyny na sloty pokojow spontanicznych</p>
            </div>
        </div>
        {#if !isLoading && !hasError}
            <div class="flex items-center gap-2">
                <Button variant="outline" size="sm" onclick={() => addRoomDialogOpen = true}>
                    <IconPlus class="size-4 mr-1.5"/>
                    Dodaj pokoj
                </Button>
                {#if rooms.length > 0}
                    <Button variant="outline" size="sm" onclick={() => generateDialogOpen = true}>
                        <IconClock class="size-4 mr-1.5"/>
                        Generuj sloty
                    </Button>
                {/if}
            </div>
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
            <!-- Team pool -->
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

            <!-- Room view (main area) -->
            <div class="flex flex-col gap-3 overflow-hidden">
                {#if rooms.length === 0}
                    <div class="rounded-lg border border-dashed p-12 text-center">
                        <IconMessages class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
                        <p class="text-muted-foreground font-medium">Brak pokojow spontanicznych</p>
                        <p class="text-sm text-muted-foreground/70 mt-1">Dodaj pokoje aby rozpoczac planowanie.</p>
                    </div>
                {:else}
                    <!-- Room selector -->
                    <div class="flex items-center gap-3">
                        <Select.Root type="single" bind:value={selectedRoomId}>
                            <Select.Trigger class="w-64">
                                {selectedRoom?.name || 'Wybierz pokoj...'}
                            </Select.Trigger>
                            <Select.Content>
                                {#each rooms as room (room.id)}
                                    <Select.Item value={String(room.id)}>
                                        {room.name}
                                    </Select.Item>
                                {/each}
                            </Select.Content>
                        </Select.Root>
                        <span class="text-sm text-muted-foreground">
                            {filledCount}/{currentSlots.length} slotow zajetych
                        </span>
                    </div>

                    <!-- Slots list -->
                    <div class="overflow-y-auto flex-1">
                        {#if currentSlots.length === 0}
                            <div class="rounded-lg border border-dashed p-8 text-center">
                                <p class="text-sm text-muted-foreground">Brak slotow dla tego pokoju</p>
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
                                        spontanInfo={slot.teamId != null ? (teamStageMap.get(slot.teamId) ?? null) : null}
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

<AddSpontanRoomDialog
    bind:open={addRoomDialogOpen}
    saving={createRoomMutation.isPending}
    onsave={handleCreateRoom}
/>

<GenerateSpontanSlotsDialog
    bind:open={generateDialogOpen}
    rooms={rooms}
    saving={generateMutation.isPending}
    preselectedRoomId={selectedRoomId}
    onsave={handleGenerateSlots}
/>
