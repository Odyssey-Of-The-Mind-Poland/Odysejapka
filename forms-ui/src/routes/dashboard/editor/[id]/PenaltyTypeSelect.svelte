<script lang="ts">
    import SelectWithLabel from "$lib/components/form/SelectWithLabel.svelte";
    import * as Select from "$lib/components/ui/select/index.js";

    interface Props {
        value: 'RANGE' | 'DISCRETE' | 'SINGLE';
        onValueChange?: (value: 'RANGE' | 'DISCRETE' | 'SINGLE') => void;
    }

    let {value = $bindable(), onValueChange}: Props = $props();

    const typeLabels = {
        SINGLE: 'Pojedyncza wartość',
        RANGE: 'Zakres',
        DISCRETE: 'Dyskretne wartości'
    };
</script>

<SelectWithLabel
    label="Typ kary"
    bind:value={value}
    flexClass="flex-1"
    triggerContent={() => typeLabels[value] || " "}
    onValueChange={(v) => onValueChange?.(v as 'RANGE' | 'DISCRETE' | 'SINGLE')}
>
    <Select.Group>
        <Select.Item value="SINGLE" label={typeLabels['SINGLE']}>
            {typeLabels['SINGLE']}
        </Select.Item>
        <Select.Item value="RANGE" label={typeLabels['RANGE']}>
            {typeLabels['RANGE']}
        </Select.Item>
        <Select.Item value="DISCRETE" label={typeLabels['DISCRETE']}>
            {typeLabels['DISCRETE']}
        </Select.Item>
    </Select.Group>
</SelectWithLabel>

