<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";

    let {value = $bindable(), min, max, disabled = false} = $props<{
        value?: number | string | null;
        min: number;
        max: number;
        disabled?: boolean;
    }>();

    const options = $derived.by(() => {
        const rangeOptions = Array.from({ length: Math.floor(max - min) + 1 }, (_, i) => min + i);
        // Always include 0, remove duplicates and sort
        const allOptions = [...new Set([0, ...rangeOptions])].sort((a, b) => a - b);
        return allOptions;
    });

    let stringValue = $derived(
        value !== null && value !== undefined ? String(value) : undefined
    );

    function handleValueChange(newValue: string | undefined) {
        value = newValue ? Number(newValue) : null;
    }
</script>

<div class="flex flex-col gap-1">
    <span class="text-[10px] text-muted-foreground leading-none">Wartość</span>
    <Select.Root
        type="single"
        value={stringValue}
        onValueChange={handleValueChange}
        disabled={disabled}
    >
        <Select.Trigger class="w-[5.5rem] h-8 text-sm" disabled={disabled}>
            {stringValue ?? 'Wybierz'}
        </Select.Trigger>
        <Select.Content>
            <Select.Group>
                {#each options as optionValue}
                    <Select.Item value={String(optionValue)} label={String(optionValue)}>
                        {optionValue}
                    </Select.Item>
                {/each}
            </Select.Group>
        </Select.Content>
    </Select.Root>
</div>
