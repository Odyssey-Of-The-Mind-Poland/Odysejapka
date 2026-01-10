<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";
    import SelectWithLabel from '$lib/components/form/SelectWithLabel.svelte';

    interface Props {
        value: 'SUBJECTIVE' | 'OBJECTIVE';
    }

    const categoryLabels = {
        SUBJECTIVE: 'SUBIEKTYWNA',
        OBJECTIVE: 'OBIEKTYWNA'
    };

    let {value = $bindable()}: Props = $props();
</script>

<SelectWithLabel 
    label="Typ kategori" 
    bind:value={value} 
    flexClass="w-[180px]"
    triggerContent={() => categoryLabels[value] || " "}
>
    <Select.Group>
        <Select.Item value="SUBJECTIVE" label={categoryLabels['SUBJECTIVE']}>
            {categoryLabels['SUBJECTIVE']}
        </Select.Item>
        <Select.Item value="OBJECTIVE" label={categoryLabels['OBJECTIVE']}>
            {categoryLabels['OBJECTIVE']}
        </Select.Item>
    </Select.Group>
</SelectWithLabel>

