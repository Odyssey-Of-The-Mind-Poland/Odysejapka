<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";
    import SelectWithLabel from '$lib/components/form/SelectWithLabel.svelte';

    interface Props {
        value: 'SECTION' | 'SCORING_GROUP' | string;
    }

    const typeLabels = {
        SECTION: 'Sekcja',
        SCORING_GROUP: 'Grupa punktowa'
    };

    let {value = $bindable()}: Props = $props();
</script>

<SelectWithLabel 
    label="Typ" 
    bind:value={value} 
    triggerContent={() => typeLabels[value] || " "}
>
    <Select.Group>
        <Select.Item value="SECTION" label={typeLabels['SECTION']}>
            {typeLabels['SECTION']}
        </Select.Item>
        <Select.Item value="SCORING_GROUP" label={typeLabels['SCORING_GROUP']}>
            {typeLabels['SCORING_GROUP']}
        </Select.Item>
    </Select.Group>
</SelectWithLabel>

