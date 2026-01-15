<script lang="ts">
    import * as Input from "$lib/components/ui/input/index.js";

    let {value = $bindable(), disabled = false} = $props<{
        value?: number | string | null;
        disabled?: boolean;
    }>();
</script>

<Input.Input
    type="number"
    bind:value={value}
    class="w-24"
    disabled={disabled}
/>

