<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import * as Collapsible from '$lib/components/ui/collapsible/index.js';
    import * as Select from "$lib/components/ui/select/index.js";
    import {Button} from '$lib/components/ui/button';
    import Trash2Icon from '@lucide/svelte/icons/trash-2';
    import ChevronDownIcon from "@lucide/svelte/icons/chevron-down";
    import GripVerticalIcon from "@lucide/svelte/icons/grip-vertical";
    import {Input} from '$lib/components/ui/input';
    import type {FormEntryType} from "./types";
    import {formatSortIndex} from "./sortIndexUtils";
    import StyleTypeSelect from "./StyleTypeSelect.svelte";

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
                    <div class="group relative flex-1 min-w-[200px]">
                        <label
                                class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
                                for={`style-name-${entry.id ?? 0}`}
                        >
                            <span class="inline-flex bg-background px-1">Nazwa kategori</span>
                        </label>
                        <Input bind:value={entry.name} class="w-full dark:bg-background pt-5" placeholder=" "
                               type="text" id={`style-name-${entry.id ?? 0}`}/>
                    </div>
                {/if}
                <div class="group relative flex-1">
                    <label
                            class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
                            for={`style-points-${entry.id ?? 0}`}
                    >
                        <span class="inline-flex bg-background px-1">Możliwe wartości punktowe</span>
                    </label>
                    <Select.Root type="single" value="1-10" disabled>
                        <Select.Trigger class="pt-5 w-full" id={`style-points-${entry.id ?? 0}`}>
                            1-10
                        </Select.Trigger>
                    </Select.Root>
                </div>
                <div class="group relative flex-1">
                    <label
                            class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
                            for={`style-judges-${entry.id ?? 0}`}
                    >
                        <span class="inline-flex bg-background px-1">Dostęp do sędziów</span>
                    </label>
                    <Select.Root type="single" value="STYLE_JUDGES" disabled>
                        <Select.Trigger class="pt-5 w-full" id={`style-judges-${entry.id ?? 0}`}>
                            Sędziowie stylu
                        </Select.Trigger>
                    </Select.Root>
                </div>
            </div>
        </Collapsible.Content>
    </Card.Root>
</Collapsible.Root>

