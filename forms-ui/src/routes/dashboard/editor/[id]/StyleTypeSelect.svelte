<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";
    import SelectWithLabel from '$lib/components/form/SelectWithLabel.svelte';

    interface Props {
        value: 'PREDEFINED' | 'FREE_TEAM_CHOICE';
    }

    const styleTypeLabels = {
        PREDEFINED: 'Predefniowany',
        FREE_TEAM_CHOICE: 'Wolny wybór drużyny'
    };

    let {value = $bindable()}: Props = $props();
</script>

<SelectWithLabel 
    label="Typ stylu" 
    bind:value={value} 
    flexClass="flex-[0_0_auto]"
    triggerContent={() => styleTypeLabels[value] || " "}
>
    <Select.Group>
        <Select.Item value="PREDEFINED" label={styleTypeLabels['PREDEFINED']}>
            {styleTypeLabels['PREDEFINED']}
        </Select.Item>
        <Select.Item value="FREE_TEAM_CHOICE" label={styleTypeLabels['FREE_TEAM_CHOICE']}>
            {styleTypeLabels['FREE_TEAM_CHOICE']}
        </Select.Item>
    </Select.Group>
</SelectWithLabel>

