<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import * as Collapsible from '$lib/components/ui/collapsible/index.js';
    import {Button} from '$lib/components/ui/button';
    import Trash2Icon from '@lucide/svelte/icons/trash-2';
    import ChevronDownIcon from "@lucide/svelte/icons/chevron-down";
    import GripVerticalIcon from "@lucide/svelte/icons/grip-vertical";
    import type {FormEntryType, RangeData, DiscreteData, SingleData} from "./types";
    import {formatSortIndex} from "./sortIndexUtils";
    import EntryNameInput from "./EntryNameInput.svelte";
    import PenaltyTypeSelect from "./PenaltyTypeSelect.svelte";
    import PenaltyRangeEditor from "./PenaltyRangeEditor.svelte";
    import PenaltyDiscreteEditor from "./PenaltyDiscreteEditor.svelte";
    import PenaltySingleEditor from "./PenaltySingleEditor.svelte";

    interface Props {
        entry: FormEntryType;
        onRemove?: () => void;
        parentIndex?: string;
    }

    let {entry = $bindable(), onRemove, parentIndex}: Props = $props();

    let displayIndex = $derived(formatSortIndex(entry, parentIndex));
    let isOpen = $state(true);

    function getCurrentType(): 'RANGE' | 'DISCRETE' | 'SINGLE' {
        if (entry.penaltyRange) return 'RANGE';
        if (entry.penaltyDiscrete) return 'DISCRETE';
        return 'SINGLE';
    }

    let currentType = $state(getCurrentType());
    let previousCurrentType = $state(currentType);
    let lastSyncedEntryType = $state(getCurrentType());

    $effect(() => {
        const entryType = getCurrentType();
        if (entryType !== lastSyncedEntryType) {
            currentType = entryType;
            previousCurrentType = entryType;
            lastSyncedEntryType = entryType;
        }
    });

    $effect(() => {
        const entryType = getCurrentType();
        if (currentType !== previousCurrentType && currentType !== entryType) {
            previousCurrentType = currentType;
            entry.penaltyRange = null;
            entry.penaltyDiscrete = null;
            entry.penaltySingle = null;
            
            switch (currentType) {
                case 'RANGE':
                    entry.penaltyRange = { min: 0, max: 10 };
                    break;
                case 'DISCRETE':
                    entry.penaltyDiscrete = { values: [0] };
                    break;
                case 'SINGLE':
                    entry.penaltySingle = { value: 0 };
                    break;
            }
        }
    });

    function handleRangeChange(data: RangeData) {
        entry.penaltyRange = data;
    }

    function handleDiscreteChange(data: DiscreteData) {
        entry.penaltyDiscrete = data;
    }

    function handleSingleChange(data: SingleData) {
        entry.penaltySingle = data;
    }
</script>

<Collapsible.Root bind:open={isOpen}>
    <Card.Root>
        <Collapsible.Trigger class="w-full flex items-center justify-between p-2 cursor-pointer">
            <div class="flex items-center gap-2">
                <GripVerticalIcon class="h-4 w-4 text-gray-400 cursor-grab active:cursor-grabbing" />
                <span class="font-semibold text-gray-700 min-w-[2rem]">{displayIndex}.</span>
            </div>
            <div class="flex items-center gap-2">
                <ChevronDownIcon class="h-4 w-4 transition-transform duration-200 {isOpen ? 'rotate-180' : ''}" />
                {#if onRemove}
                    <Button
                            variant="ghost"
                            size="icon"
                            onclick={(e) => {
                                e.stopPropagation();
                                onRemove?.();
                            }}
                            class="shrink-0"
                            title="Usuń wpis"
                    >
                        <Trash2Icon class="h-4 w-4 text-destructive"/>
                    </Button>
                {/if}
            </div>
        </Collapsible.Trigger>
        <Collapsible.Content>
            <div class="flex gap-4 p-2">
                <div class="flex items-center gap-2 w-200">
                    <EntryNameInput bind:value={entry.name} id={entry.id} />
                </div>
                <div class="flex items-center gap-2">
                    <PenaltyTypeSelect 
                        bind:value={currentType}
                    />
                </div>
                {#if entry.penaltyRange}
                    <PenaltyRangeEditor 
                        value={entry.penaltyRange} 
                        onValueChange={handleRangeChange}
                    />
                {:else if entry.penaltyDiscrete}
                    <PenaltyDiscreteEditor 
                        value={entry.penaltyDiscrete} 
                        onValueChange={handleDiscreteChange}
                    />
                {:else if entry.penaltySingle}
                    <PenaltySingleEditor 
                        value={entry.penaltySingle} 
                        onValueChange={handleSingleChange}
                    />
                {/if}
            </div>
        </Collapsible.Content>
    </Card.Root>
</Collapsible.Root>

