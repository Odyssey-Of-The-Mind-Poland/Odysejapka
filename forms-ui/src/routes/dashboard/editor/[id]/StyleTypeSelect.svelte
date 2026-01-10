<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";

    interface Props {
        value: 'PREDEFINED' | 'FREE_TEAM_CHOICE';
    }

    const styleTypeLabels = {
        PREDEFINED: 'Predefniowany',
        FREE_TEAM_CHOICE: 'Wolny wybór drużyny'
    };

    let {value = $bindable()}: Props = $props();
    
    let selectId = `style-type-select-${Math.random().toString(36).substr(2, 9)}`;
</script>

<div class="group relative flex-[0_0_auto]">
    <label
            class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
            for={selectId}
    >
        <span class="inline-flex bg-background px-1">Typ stylu</span>
    </label>
    <Select.Root type="single" bind:value={value}>
        <Select.Trigger class="pt-5 w-full" id={selectId}>
            {styleTypeLabels[value] || " "}
        </Select.Trigger>
        <Select.Content>
            <Select.Group>
                <Select.Item value="PREDEFINED" label={styleTypeLabels['PREDEFINED']}>
                    {styleTypeLabels['PREDEFINED']}
                </Select.Item>
                <Select.Item value="FREE_TEAM_CHOICE" label={styleTypeLabels['FREE_TEAM_CHOICE']}>
                    {styleTypeLabels['FREE_TEAM_CHOICE']}
                </Select.Item>
            </Select.Group>
        </Select.Content>
    </Select.Root>
</div>

