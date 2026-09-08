<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";

    let {value = $bindable(), disabled = false} = $props<{
        value?: number | string | null;
        disabled?: boolean;
    }>();

    const options = Array.from({ length: 10 }, (_, i) => i + 1);

    let stringValue = $derived(
        value !== null && value !== undefined ? String(value) : undefined
    );

    function handleValueChange(newValue: string | undefined) {
        value = newValue ? Number(newValue) : null;
    }
</script>

<div class="flex flex-col gap-1">
    <span class="text-[10px] text-muted-foreground leading-none">Punkty</span>
    <Select.Root
        type="single"
        value={stringValue}
        onValueChange={handleValueChange}
        disabled={disabled}
    >
        <Select.Trigger class="w-full h-10 text-sm lg:w-[5.5rem] lg:h-8" disabled={disabled}>
            {stringValue ?? 'Punkty'}
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
