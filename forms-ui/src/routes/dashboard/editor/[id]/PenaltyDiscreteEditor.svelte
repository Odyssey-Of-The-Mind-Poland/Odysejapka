<script lang="ts">
    import {CheckIcon, ChevronsUpDownIcon, XIcon} from '@lucide/svelte';
    import {Badge} from '$lib/components/ui/badge';
    import {Button} from "$lib/components/ui/button";
    import * as Command from '$lib/components/ui/command/index.js';
    import * as Popover from '$lib/components/ui/popover/index.js';
    import type {DiscreteData} from "./types";

    interface Props {
        value: DiscreteData | null | undefined;
        onValueChange: (data: DiscreteData) => void;
    }

    let {value, onValueChange}: Props = $props();

    const availableNumbers = $derived(Array.from({length: 100}, (_, i) => i + 1));
    let open = $state(false);

    function emit(values: number[]) {
        const normalized = [...new Set(values)]
            .filter((v) => Number.isFinite(v))
            .sort((a, b) => a - b);

        onValueChange({ values: normalized.length ? normalized : [1] });
    }

    function toggleSelection(n: number) {
        const current = value?.values?.length ? value.values : [1];
        const next = current.includes(n) ? current.filter((x) => x !== n) : [...current, n];
        emit(next);
    }

    function removeSelection(n: number) {
        const current = value?.values?.length ? value.values : [1];
        emit(current.filter((x) => x !== n));
    }
</script>

<div class="flex flex-col gap-2 min-w-[16rem]">
    <Popover.Root bind:open>
        <Popover.Trigger>
            {#snippet child({props})}
                <Button
                    {...props}
                    variant="outline"
                    role="combobox"
                    aria-expanded={open}
                    class="h-auto min-h-8 w-full justify-between hover:bg-transparent"
                >
                    <div class="flex flex-wrap items-center gap-1 pe-2.5">
                        {#if (value?.values?.length ?? 0) > 0}
                            {#each value!.values as n (n)}
                                <Badge variant="outline">
                                    {n}
                                    <Button
                                        variant="ghost"
                                        size="icon"
                                        class="size-4"
                                        onclick={(e: MouseEvent) => {
                                            e.stopPropagation();
                                            removeSelection(n);
                                        }}
                                        title="Usuń wartość"
                                    >
                                        <XIcon class="size-3"/>
                                    </Button>
                                </Badge>
                            {/each}
                        {:else}
                            <span class="text-muted-foreground">Wybierz wartości</span>
                        {/if}
                    </div>
                    <ChevronsUpDownIcon
                        size={16}
                        class="shrink-0 text-muted-foreground/80"
                        aria-hidden="true"
                    />
                </Button>
            {/snippet}
        </Popover.Trigger>
        <Popover.Content class="w-(--radix-popper-anchor-width) p-0">
            <Command.Root>
                <Command.List>
                    <Command.Empty>Brak wyników.</Command.Empty>
                    <Command.Group>
                        {#each availableNumbers as n (n)}
                            <Command.Item
                                value={n.toString()}
                                onSelect={() => toggleSelection(n)}
                            >
                                <span class="truncate">{n}</span>
                                {#if value?.values?.includes(n)}
                                    <CheckIcon size={16} class="ml-auto"/>
                                {/if}
                            </Command.Item>
                        {/each}
                    </Command.Group>
                </Command.List>
            </Command.Root>
        </Popover.Content>
    </Popover.Root>
</div>

