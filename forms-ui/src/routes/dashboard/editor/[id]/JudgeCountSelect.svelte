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
        selectedCities,
        availableCities,
        onSelectionChange
    } = $props<{
        id: string;
        title: string;
        judgeCount: number;
        selectedCities: string[];
        availableCities: City[];
        onSelectionChange: (selected: string[]) => void;
    }>();

    let open = $state(false);

    function toggleSelection(value: string) {
        const newSelection = selectedCities.includes(value)
            ? selectedCities.filter((v: string) => v !== value)
            : [...selectedCities, value];
        onSelectionChange(newSelection);
    }

    function removeSelection(value: string) {
        const newSelection = selectedCities.filter((v: string) => v !== value);
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
                                {#if selectedCities.length > 0}
                                    {#each selectedCities as val (val)}
                                        {@const city = availableCities.find((c: City) => c.name === val) || { name: val }}
                                        <Badge variant="outline">
                                            {city.name}
                                            <Button
                                                    variant="ghost"
                                                    size="icon"
                                                    class="size-4"
                                                    onclick={(e: MouseEvent) => {
                                                        e.stopPropagation();
                                                        removeSelection(val);
                                                    }}
                                            >
                                                <XIcon class="size-3"/>
                                            </Button>
                                        </Badge>
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
                                {#each availableCities as city (city.name)}
                                    <Command.Item
                                            value={city.name}
                                            onSelect={() => toggleSelection(city.name)}
                                    >
                                        <span class="truncate">{city.name}</span>
                                        {#if selectedCities.includes(city.name)}
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