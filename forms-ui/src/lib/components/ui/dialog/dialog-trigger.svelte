<script lang="ts">
    import {Dialog as DialogPrimitive} from "bits-ui";

    let {ref = $bindable(null), ...restProps}: DialogPrimitive.TriggerProps = $props();
</script>

<DialogPrimitive.Trigger {...restProps} bind:ref data-slot="dialog-trigger"/>
