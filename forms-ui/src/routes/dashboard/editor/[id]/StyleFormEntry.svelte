<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import * as Collapsible from '$lib/components/ui/collapsible/index.js';
    import {Button} from '$lib/components/ui/button';
    import Trash2Icon from '@lucide/svelte/icons/trash-2';
    import ChevronDownIcon from "@lucide/svelte/icons/chevron-down";
    import GripVerticalIcon from "@lucide/svelte/icons/grip-vertical";
    import type {FormEntryType} from "./types";
    import {formatSortIndex} from "./sortIndexUtils";
    import StyleTypeSelect from "./StyleTypeSelect.svelte";
    import InputWithLabel from '$lib/components/form/InputWithLabel.svelte';
    import SelectWithLabel from '$lib/components/form/SelectWithLabel.svelte';
    import * as Select from "$lib/components/ui/select/index.js";

    interface Props {
        entry: FormEntryType;
        onRemove?: () => void;
        parentIndex?: string;
    }

    let {entry = $bindable(), onRemove, parentIndex}: Props = $props();

    let displayIndex = $derived(formatSortIndex(entry, parentIndex));
    let isOpen = $state(true);
    let isPredefined = $derived(entry.styleType === 'PREDEFINED');

    $effect(() => {
        if (entry.type === 'STYLE' && !entry.styleType) {
            entry.styleType = 'PREDEFINED';
        }
    });
</script>

<Collapsible.Root bind:open={isOpen}>
    <Card.Root>
        <Collapsible.Trigger class="w-full flex items-center justify-between p-2 cursor-pointer">
            <div class="flex items-center gap-2">
                <GripVerticalIcon class="h-4 w-4 text-gray-400 cursor-grab active:cursor-grabbing"/>
                <span class="font-semibold text-gray-700 min-w-[2rem]">{displayIndex}.</span>
            </div>
            <div class="flex items-center gap-2">
                <ChevronDownIcon class="h-4 w-4 transition-transform duration-200 {isOpen ? 'rotate-180' : ''}"/>
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
            <div class="flex items-center gap-4 p-2 flex-wrap">
                <div class="flex-1">
                    {#if entry.styleType}
                        <StyleTypeSelect bind:value={entry.styleType}/>
                    {/if}
                </div>
                {#if isPredefined}
                    <InputWithLabel 
                        label="Nazwa kategori" 
                        bind:value={entry.name} 
                        id={entry.id} 
                        flexClass="flex-1 min-w-[200px]"
                    />
                {/if}
                <SelectWithLabel 
                    label="Możliwe wartości punktowe" 
                    value="1-10" 
                    disabled={true}
                    id={entry.id}
                    triggerContent={() => "1-10"}
                >
                    <Select.Group>
                        <Select.Item value="1-10" label="1-10">1-10</Select.Item>
                    </Select.Group>
                </SelectWithLabel>
                <SelectWithLabel 
                    label="Dostęp do sędziów" 
                    value="STYLE_JUDGES" 
                    disabled={true}
                    id={entry.id}
                    triggerContent={() => "Sędziowie stylu"}
                >
                    <Select.Group>
                        <Select.Item value="STYLE_JUDGES" label="Sędziowie stylu">Sędziowie stylu</Select.Item>
                    </Select.Group>
                </SelectWithLabel>
            </div>
        </Collapsible.Content>
    </Card.Root>
</Collapsible.Root>

