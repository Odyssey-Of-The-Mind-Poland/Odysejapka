<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";

    let {value = $bindable(), singleValue, disabled = false} = $props<{
        value?: number | string | null;
        singleValue: number;
        disabled?: boolean;
    }>();

    // Always include 0, remove duplicates and sort
    const options = $derived.by(() => {
        return [...new Set([0, singleValue])].sort((a, b) => a - b);
    });

    let stringValue = $derived(
        value !== null && value !== undefined ? String(value) : undefined
    );

    function handleValueChange(newValue: string | undefined) {
        value = newValue ? Number(newValue) : null;
    }
</script>

<Select.Root
    type="single"
    value={stringValue}
    onValueChange={handleValueChange}
    disabled={disabled}
>
    <Select.Trigger class="w-24" disabled={disabled}>
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

