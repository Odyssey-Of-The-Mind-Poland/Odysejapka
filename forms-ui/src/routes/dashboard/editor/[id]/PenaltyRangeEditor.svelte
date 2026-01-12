<script lang="ts">
    import InputWithLabel from "$lib/components/form/InputWithLabel.svelte";
    import type {RangeData} from "./types";

    interface Props {
        value: RangeData | null | undefined;
        onValueChange: (data: RangeData) => void;
    }

    let {value, onValueChange}: Props = $props();

    let min = $state(value?.min.toString() ?? '0');
    let max = $state(value?.max.toString() ?? '0');

    $effect(() => {
        if (value) {
            min = value.min.toString();
            max = value.max.toString();
        }
    });

    function updateValue() {
        const minNum = parseFloat(min) || 0;
        const maxNum = parseFloat(max) || 0;
        onValueChange({
            min: minNum,
            max: maxNum
        });
    }
</script>

<div class="flex gap-4">
    <InputWithLabel
        label="Min"
        value={min}
        onInput={(e) => {
            min = e.currentTarget.value;
            updateValue();
        }}
        type="number"
        step="0.1"
        flexClass="flex-1"
    />
    <InputWithLabel
        label="Max"
        value={max}
        onInput={(e) => {
            max = e.currentTarget.value;
            updateValue();
        }}
        type="number"
        step="0.1"
        flexClass="flex-1"
    />
</div>

