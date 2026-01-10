<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";
    import SelectWithLabel from '$lib/components/form/SelectWithLabel.svelte';

    interface Props {
        value: 'A' | 'B' | 'A_PLUS_B';
    }

    const judgeLabels = {
        A: 'A',
        B: 'B',
        A_PLUS_B: 'A+B'
    };

    let {value = $bindable()}: Props = $props();
</script>

<SelectWithLabel 
    label="Sędziowie" 
    bind:value={value} 
    flexClass="w-full"
    triggerContent={() => judgeLabels[value] || " "}
>
    <Select.Group>
        <Select.Item value="A" label={judgeLabels['A']}>
            {judgeLabels['A']}
        </Select.Item>
        <Select.Item value="B" label={judgeLabels['B']}>
            {judgeLabels['B']}
        </Select.Item>
        <Select.Item value="A_PLUS_B" label={judgeLabels['A_PLUS_B']}>
            {judgeLabels['A_PLUS_B']}
        </Select.Item>
    </Select.Group>
</SelectWithLabel>

