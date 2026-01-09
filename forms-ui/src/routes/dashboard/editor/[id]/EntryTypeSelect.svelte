<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";

    interface Props {
        value: 'SECTION' | 'SCORING_GROUP';
    }

    const typeLabels = {
        SECTION: 'Sekcja',
        SCORING_GROUP: 'Grupa punktowa'
    };

    let {value = $bindable()}: Props = $props();
    
    let selectId = `entry-type-select-${Math.random().toString(36).substr(2, 9)}`;
</script>

<div class="group relative">
    <label
            class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
            for={selectId}
    >
        <span class="inline-flex bg-background px-1">Typ</span>
    </label>
    <Select.Root type="single" bind:value={value}>
        <Select.Trigger class="w-[180px] pt-5" id={selectId}>
            {typeLabels[value] || " "}
        </Select.Trigger>
        <Select.Content>
            <Select.Group>
                <Select.Item value="SECTION" label={typeLabels['SECTION']}>
                    {typeLabels['SECTION']}
                </Select.Item>
                <Select.Item value="SCORING_GROUP" label={typeLabels['SCORING_GROUP']}>
                    {typeLabels['SCORING_GROUP']}
                </Select.Item>
            </Select.Group>
        </Select.Content>
    </Select.Root>
</div>

