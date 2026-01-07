<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";

    interface Props {
        value: 'SUBJECTIVE' | 'OBJECTIVE';
    }

    const categoryLabels = {
        SUBJECTIVE: 'SUBIEKTYWNA',
        OBJECTIVE: 'OBIEKTYWNA'
    };

    let {value = $bindable()}: Props = $props();
    
    let selectId = `select-${Math.random().toString(36).substr(2, 9)}`;
</script>

<div class="group relative w-[180px]">
    <label
            class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
            for={selectId}
    >
        <span class="inline-flex bg-background px-1">Typ kategori</span>
    </label>
    <Select.Root type="single" bind:value={value}>
        <Select.Trigger class="w-[180px] pt-5" id={selectId}>
            {categoryLabels[value] || " "}
        </Select.Trigger>
        <Select.Content>
            <Select.Group>
                <Select.Item value="SUBJECTIVE" label={categoryLabels['SUBJECTIVE']}>
                    {categoryLabels['SUBJECTIVE']}
                </Select.Item>
                <Select.Item value="OBJECTIVE" label={categoryLabels['OBJECTIVE']}>
                    {categoryLabels['OBJECTIVE']}
                </Select.Item>
            </Select.Group>
        </Select.Content>
    </Select.Root>
</div>

