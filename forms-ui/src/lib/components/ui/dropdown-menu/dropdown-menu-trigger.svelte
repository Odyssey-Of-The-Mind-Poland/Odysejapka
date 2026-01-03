<script lang="ts">
    import {DropdownMenu as DropdownMenuPrimitive} from "bits-ui";

    let {ref = $bindable(null), ...restProps}: DropdownMenuPrimitive.TriggerProps = $props();
</script>

<DropdownMenuPrimitive.Trigger {...restProps} bind:ref data-slot="dropdown-menu-trigger"/>
