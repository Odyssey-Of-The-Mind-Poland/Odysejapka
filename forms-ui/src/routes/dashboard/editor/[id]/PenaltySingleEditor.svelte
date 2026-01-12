<script lang="ts">
    import InputWithLabel from "$lib/components/form/InputWithLabel.svelte";
    import type {SingleData} from "./types";

    interface Props {
        value: SingleData | null | undefined;
        onValueChange: (data: SingleData) => void;
    }

    let {value, onValueChange}: Props = $props();

    let singleValue = $state(value?.value.toString() ?? '0');

    $effect(() => {
        if (value) {
            singleValue = value.value.toString();
        }
    });

    function updateValue() {
        const numValue = parseFloat(singleValue) || 0;
        onValueChange({
            value: numValue
        });
    }
</script>

<InputWithLabel
    label="Wartość"
    value={singleValue}
    onInput={(e) => {
        singleValue = e.currentTarget.value;
        updateValue();
    }}
    type="number"
    step="0.1"
    flexClass="flex-1"
/>

