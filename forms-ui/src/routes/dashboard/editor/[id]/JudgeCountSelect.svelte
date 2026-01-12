<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {CheckIcon, ChevronsUpDownIcon, XIcon} from '@lucide/svelte';
    import {Badge} from '$lib/components/ui/badge';
    import {Button} from '$lib/components/ui/button';
    import * as Command from '$lib/components/ui/command/index.js';
    import {Label} from '$lib/components/ui/label';
    import * as Popover from '$lib/components/ui/popover/index.js';
    import type {City} from "./types";

    const {
        id,
        title,
        judgeCount,
        selectedCityIds,
        availableCities,
        onSelectionChange
    } = $props<{
        id: string;
        title: string;
        judgeCount: number;
        selectedCityIds: number[];
        availableCities: City[];
        onSelectionChange: (selected: number[]) => void;
    }>();

    let open = $state(false);

    function toggleSelection(cityId: number) {
        const newSelection = selectedCityIds.includes(cityId)
            ? selectedCityIds.filter((id: number) => id !== cityId)
            : [...selectedCityIds, cityId];
        onSelectionChange(newSelection);
    }

    function removeSelection(cityId: number) {
        const newSelection = selectedCityIds.filter((id: number) => id !== cityId);
        onSelectionChange(newSelection);
    }

    function getJudgeText(count: number): string {
        if (count === 1) {
            return "1 Sędzia A * 1 Sędzia B * 1 Sędzia Stylu";
        } else if (count === 2) {
            return "2 Sędziowie A * 2 Sędziowie B * 2 Sędziowie Stylu";
        }
        return `${count} Sędziów`;
    }
</script>

<Card.Root class="w-full max-w-full">
    <Card.Header>
        <Card.Title>{title}</Card.Title>
    </Card.Header>
    <Card.Content class="flex flex-col gap-4 w-full max-w-full">
        <div class="text-sm text-muted-foreground">
            {getJudgeText(judgeCount)}
        </div>
        <div class="w-full space-y-2">
            <Label for={id}>Konkurs</Label>
            <Popover.Root bind:open>
                <Popover.Trigger>
                    {#snippet child({props})}
                        <Button
                                {...props}
                                {id}
                                variant="outline"
                                role="combobox"
                                aria-expanded={open}
                                class="h-auto min-h-8 w-full justify-between hover:bg-transparent"
                        >
                            <div class="flex flex-wrap items-center gap-1 pe-2.5">
                                {#if selectedCityIds.length > 0}
                                    {#each selectedCityIds as cityId (cityId)}
                                        {@const city = availableCities.find((c: City) => c.id === cityId)}
                                        {#if city}
                                            <Badge variant="outline">
                                                {city.name}
                                                <Button
                                                        variant="ghost"
                                                        size="icon"
                                                        class="size-4"
                                                        onclick={(e: MouseEvent) => {
                                                            e.stopPropagation();
                                                            removeSelection(cityId);
                                                        }}
                                                >
                                                    <XIcon class="size-3"/>
                                                </Button>
                                            </Badge>
                                        {/if}
                                    {/each}
                                {:else}
                                    <span class="text-muted-foreground">Wybierz miasta</span>
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
                            <Command.Empty>Nie znaleziono miasta.</Command.Empty>
                            <Command.Group>
                                {#each availableCities as city (city.id)}
                                    <Command.Item
                                            value={city.name}
                                            onSelect={() => toggleSelection(city.id)}
                                    >
                                        <span class="truncate">{city.name}</span>
                                        {#if selectedCityIds.includes(city.id)}
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

    </Card.Content>
</Card.Root>