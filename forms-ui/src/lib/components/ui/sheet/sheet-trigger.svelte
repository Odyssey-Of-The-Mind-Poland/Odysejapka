<script lang="ts">
    import {Dialog as SheetPrimitive} from "bits-ui";

    let {ref = $bindable(null), ...restProps}: SheetPrimitive.TriggerProps = $props();
</script>

<SheetPrimitive.Trigger {...restProps} bind:ref data-slot="sheet-trigger"/>
