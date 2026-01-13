<script lang="ts">
    import SelectWithLabel from "$lib/components/form/SelectWithLabel.svelte";
    import * as Select from "$lib/components/ui/select/index.js";

    interface Props {
        value: 'RANGE' | 'DISCRETE' | 'SINGLE' | 'ZERO_BALSA';
        onValueChange?: (value: 'RANGE' | 'DISCRETE' | 'SINGLE' | 'ZERO_BALSA') => void;
    }

    let {value = $bindable(), onValueChange}: Props = $props();

    const typeLabels = {
        SINGLE: 'Pojedyncza wartość',
        RANGE: 'Zakres',
        DISCRETE: 'Dyskretne wartości',
        ZERO_BALSA: 'Zero punktów za balse'
    };
</script>

<SelectWithLabel
    label="Typ kary"
    bind:value={value}
    flexClass="flex-1"
    triggerContent={() => typeLabels[value] || " "}
    onValueChange={(v) => onValueChange?.(v as 'RANGE' | 'DISCRETE' | 'SINGLE' | 'ZERO_BALSA')}
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
        <Select.Item value="ZERO_BALSA" label={typeLabels['ZERO_BALSA']}>
            {typeLabels['ZERO_BALSA']}
        </Select.Item>
    </Select.Group>
</SelectWithLabel>

