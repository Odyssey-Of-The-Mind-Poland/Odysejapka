<script lang="ts">
    import {Command as CommandPrimitive} from "bits-ui";
    import {cn} from "$lib/utils/index.js";

    let {
        ref = $bindable(null),
        class: className,
        ...restProps
    }: CommandPrimitive.SeparatorProps = $props();
</script>

<CommandPrimitive.Separator
        {...restProps}
        bind:ref
        class={cn("bg-border -mx-1 h-px", className)}
        data-slot="command-separator"
/>
