<script lang="ts">
    import * as Input from "$lib/components/ui/input/index.js";

    let {value = $bindable(), disabled = false} = $props<{
        value?: string | null;
        disabled?: boolean;
    }>();
</script>

<Input.Input
    type="text"
    bind:value={value}
    class="w-48"
    disabled={disabled}
    placeholder="Nazwa stylu"
/>

