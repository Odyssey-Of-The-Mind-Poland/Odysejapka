<script lang="ts">
    import {Button} from "$lib/components/ui/button/index.js";
    import {Input} from "$lib/components/ui/input/index.js";
    import * as Dialog from "$lib/components/ui/dialog/index.js";
    import * as Select from "$lib/components/ui/select/index.js";
    import type {Stage} from "../types";

    type Props = {
        open: boolean;
        stages: Stage[];
        saving: boolean;
        preselectedStageId?: string;
        onsave: (stageId: number, startTime: string, slotDurationMinutes: number, count: number) => void;
    };

    let {open = $bindable(false), stages, saving, preselectedStageId = '', onsave}: Props = $props();

    let selectedStageId = $state<string>('');

    $effect(() => {
        if (open && preselectedStageId) {
            selectedStageId = preselectedStageId;
        }
    });
    let startTime = $state('08:00');
    let slotDuration = $state(20);
    let slotCount = $state(10);

    let canSave = $derived(selectedStageId !== '' && startTime && slotDuration > 0 && slotCount > 0);

    function handleSave() {
        if (!canSave) return;
        onsave(Number(selectedStageId), startTime, slotDuration, slotCount);
    }

    // Preview generated times
    let preview = $derived.by(() => {
        if (!startTime || slotDuration <= 0 || slotCount <= 0) return [];
        const times: string[] = [];
        const [h, m] = startTime.split(':').map(Number);
        let totalMinutes = h * 60 + m;
        for (let i = 0; i < Math.min(slotCount, 20); i++) {
            const startH = Math.floor(totalMinutes / 60).toString().padStart(2, '0');
            const startM = (totalMinutes % 60).toString().padStart(2, '0');
            totalMinutes += slotDuration;
            const endH = Math.floor(totalMinutes / 60).toString().padStart(2, '0');
            const endM = (totalMinutes % 60).toString().padStart(2, '0');
            times.push(`${startH}:${startM} - ${endH}:${endM}`);
        }
        return times;
    });
</script>

<Dialog.Root bind:open>
    <Dialog.Content class="sm:max-w-md">
        <Dialog.Header>
            <Dialog.Title>Generuj sloty czasowe</Dialog.Title>
            <Dialog.Description>
                Ustaw godzine rozpoczecia, czas trwania slotu i liczbe slotow
            </Dialog.Description>
        </Dialog.Header>

        <div class="flex flex-col gap-4 py-4">
            <div>
                <label class="text-sm font-medium mb-1 block">Scena</label>
                <Select.Root type="single" bind:value={selectedStageId}>
                    <Select.Trigger>
                        {stages.find(s => String(s.id) === selectedStageId)?.name || 'Wybierz scene...'}
                    </Select.Trigger>
                    <Select.Content>
                        {#each stages.sort((a, b) => a.number - b.number) as stage (stage.id)}
                            <Select.Item value={String(stage.id)}>
                                {stage.name || `Scena ${stage.number}`}
                            </Select.Item>
                        {/each}
                    </Select.Content>
                </Select.Root>
            </div>

            <div>
                <label class="text-sm font-medium mb-1 block">Godzina rozpoczecia</label>
                <Input type="time" bind:value={startTime}/>
            </div>

            <div class="grid grid-cols-2 gap-3">
                <div>
                    <label class="text-sm font-medium mb-1 block">Czas slotu (min)</label>
                    <Input type="number" bind:value={slotDuration} min={5} max={120}/>
                </div>
                <div>
                    <label class="text-sm font-medium mb-1 block">Liczba slotow</label>
                    <Input type="number" bind:value={slotCount} min={1} max={50}/>
                </div>
            </div>

            {#if preview.length > 0}
                <div class="rounded-md border bg-muted/30 p-3 max-h-40 overflow-y-auto">
                    <p class="text-xs font-medium text-muted-foreground mb-1">Podglad ({slotCount} slotow):</p>
                    <div class="flex flex-wrap gap-1">
                        {#each preview as time, i}
                            <span class="text-xs font-mono bg-background rounded px-1.5 py-0.5 border">{time}</span>
                        {/each}
                        {#if slotCount > 20}
                            <span class="text-xs text-muted-foreground">...i {slotCount - 20} wiecej</span>
                        {/if}
                    </div>
                </div>
            {/if}
        </div>

        <Dialog.Footer>
            <Dialog.Close>
                {#snippet child({ props })}
                    <Button {...props} variant="outline">Anuluj</Button>
                {/snippet}
            </Dialog.Close>
            <Button onclick={handleSave} disabled={!canSave || saving}>
                Generuj {slotCount} slotow
            </Button>
        </Dialog.Footer>
    </Dialog.Content>
</Dialog.Root>
