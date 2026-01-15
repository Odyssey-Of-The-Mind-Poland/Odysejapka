<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";

    let {value = $bindable(), singleValue, disabled = false} = $props<{
        value?: number | string | null;
        singleValue: number;
        disabled?: boolean;
    }>();

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
            <Select.Item value={String(singleValue)} label={String(singleValue)}>
                {singleValue}
            </Select.Item>
        </Select.Group>
    </Select.Content>
</Select.Root>

