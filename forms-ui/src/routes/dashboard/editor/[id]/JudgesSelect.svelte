<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";

    interface Props {
        value: 'A' | 'B' | 'A_PLUS_B';
    }

    const judgeLabels = {
        A: 'A',
        B: 'B',
        A_PLUS_B: 'A+B'
    };

    let {value = $bindable()}: Props = $props();
    
    let selectId = `judges-select-${Math.random().toString(36).substr(2, 9)}`;
</script>

<div class="group relative w-full">
    <label
            class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
            for={selectId}
    >
        <span class="inline-flex bg-background px-1">Sędziowie</span>
    </label>
    <Select.Root type="single" bind:value={value}>
        <Select.Trigger class="w-full pt-5" id={selectId}>
            {judgeLabels[value] || " "}
        </Select.Trigger>
        <Select.Content>
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
        </Select.Content>
    </Select.Root>
</div>

