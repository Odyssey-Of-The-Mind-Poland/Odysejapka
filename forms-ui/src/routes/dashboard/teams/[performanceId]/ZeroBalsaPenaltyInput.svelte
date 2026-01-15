<script lang="ts">
    import { Checkbox } from "$lib/components/ui/checkbox/index.js";

    let {checked = $bindable(), disabled = false} = $props<{
        checked?: boolean;
        disabled?: boolean;
    }>();
</script>

<Checkbox bind:checked disabled={disabled} />

