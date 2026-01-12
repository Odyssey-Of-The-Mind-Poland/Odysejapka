<script lang="ts">
    import InputWithLabel from "$lib/components/form/InputWithLabel.svelte";
    import {Button} from "$lib/components/ui/button";
    import PlusIcon from "@lucide/svelte/icons/plus";
    import XIcon from "@lucide/svelte/icons/x";
    import type {DiscreteData} from "./types";

    interface Props {
        value: DiscreteData | null | undefined;
        onValueChange: (data: DiscreteData) => void;
    }

    let {value, onValueChange}: Props = $props();

    let values = $state<string[]>(value?.values.map(v => v.toString()) ?? ['0']);

    $effect(() => {
        if (value) {
            values = value.values.map(v => v.toString());
        }
    });

    function updateValue() {
        const numValues = values.map(v => parseFloat(v) || 0);
        onValueChange({
            values: numValues
        });
    }

    function addValue() {
        values = [...values, '0'];
        updateValue();
    }

    function removeValue(index: number) {
        if (values.length > 1) {
            values = values.filter((_, i) => i !== index);
            updateValue();
        }
    }

    function updateValueAtIndex(index: number, newValue: string) {
        values[index] = newValue;
        updateValue();
    }
</script>

<div class="flex flex-col gap-2">
    <div class="text-sm font-medium text-gray-700">Wartości:</div>
    <div class="flex flex-col gap-2">
        {#each values as valueStr, index}
            <div class="flex gap-2 items-center">
                <InputWithLabel
                    label="Wartość {index + 1}"
                    value={valueStr}
                    onInput={(e) => updateValueAtIndex(index, e.currentTarget.value)}
                    type="number"
                    step="0.1"
                    flexClass="flex-1"
                />
                {#if values.length > 1}
                    <Button
                        variant="ghost"
                        size="icon"
                        onclick={() => removeValue(index)}
                        class="shrink-0 mt-5"
                        title="Usuń wartość"
                    >
                        <XIcon class="h-4 w-4 text-destructive"/>
                    </Button>
                {/if}
            </div>
        {/each}
    </div>
    <Button
        variant="outline"
        onclick={addValue}
        class="w-fit"
    >
        <PlusIcon class="h-4 w-4 mr-2"/>
        Dodaj wartość
    </Button>
</div>

