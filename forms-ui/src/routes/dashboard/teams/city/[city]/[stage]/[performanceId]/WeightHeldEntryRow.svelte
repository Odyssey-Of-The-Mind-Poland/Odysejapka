<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";
    import {Button} from "$lib/components/ui/button/index.js";
    import type {WeightHeldTeamFormEntry, ValidationFailure} from "$lib/utils/form-results";
    import CircleAlertIcon from "@lucide/svelte/icons/circle-alert";
    import PlusIcon from "@lucide/svelte/icons/plus";
    import XIcon from "@lucide/svelte/icons/x";

    let {
        weightEntry = $bindable(),
        validationErrors = [],
    } = $props<{
        weightEntry: WeightHeldTeamFormEntry;
        validationErrors?: ValidationFailure[];
    }>();

    const ALL_WEIGHTS = [2.5, 5, 10, 15, 20, 25];

    let entryErrors = $derived(
        validationErrors.filter((e: ValidationFailure) => e.entryId === weightEntry.entry.id)
    );
    let hasError = $derived(entryErrors.length > 0);

    let availableWeights = $derived.by(() => {
        if (weightEntry.weights.length === 0) {
            return [2.5];
        }
        return ALL_WEIGHTS;
    });

    let selectedWeight = $state<string | undefined>(undefined);

    function addWeight() {
        if (!selectedWeight) return;
        const weight = Number(selectedWeight);
        weightEntry.weights = [...weightEntry.weights, weight];
        selectedWeight = undefined;
    }

    function removeWeight(index: number) {
        weightEntry.weights = weightEntry.weights.filter((_: number, i: number) => i !== index);
    }

    function formatWeight(weight: number): string {
        return weight.toString().replace('.', ',');
    }
</script>

<div class="flex flex-col gap-3 px-5 py-4 {hasError ? 'border-l-3 border-l-destructive bg-destructive/5' : ''}">
    {#if hasError}
        <div class="flex items-center gap-1">
            <CircleAlertIcon class="size-3 text-destructive shrink-0" />
            <span class="text-xs text-destructive">{entryErrors[0].message}</span>
        </div>
    {/if}

    <!-- Weights list, each on its own row -->
    {#if weightEntry.weights.length > 0}
        <div class="flex flex-col gap-1.5">
            {#each weightEntry.weights as weight, i}
                <div class="flex items-center gap-2">
                    <span class="text-xs text-muted-foreground font-mono w-6 text-right">{i + 1}.</span>
                    <div class="flex items-center gap-1.5 rounded-md border bg-muted/50 px-2.5 py-1.5">
                        <span class="text-sm font-medium">{formatWeight(weight)} kg</span>
                        <button
                            class="ml-1 text-muted-foreground hover:text-destructive transition-colors cursor-pointer"
                            onclick={() => removeWeight(i)}
                            title="Usuń ciężar"
                        >
                            <XIcon class="size-3.5" />
                        </button>
                    </div>
                </div>
            {/each}
        </div>
    {/if}

    <!-- Add weight control -->
    <div class="flex items-center gap-2">
        <Select.Root
            type="single"
            bind:value={selectedWeight}
        >
            <Select.Trigger class="w-[10rem] h-8 text-sm">
                {selectedWeight ? `${formatWeight(Number(selectedWeight))} kg` : 'Ciężar...'}
            </Select.Trigger>
            <Select.Content>
                <Select.Group>
                    {#each availableWeights as w}
                        <Select.Item value={String(w)} label="{formatWeight(w)} kg">
                            {formatWeight(w)} kg
                        </Select.Item>
                    {/each}
                </Select.Group>
            </Select.Content>
        </Select.Root>
        <Button
            variant="outline"
            size="sm"
            onclick={addWeight}
            disabled={!selectedWeight}
            class="h-8"
        >
            <PlusIcon class="size-3.5 mr-1" />
            Dodaj
        </Button>
    </div>
</div>
