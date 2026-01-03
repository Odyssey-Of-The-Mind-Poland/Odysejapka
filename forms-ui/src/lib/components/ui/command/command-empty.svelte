<script lang="ts">
    import {Command as CommandPrimitive} from "bits-ui";
    import {cn} from "$lib/utils/index.js";

    let {
        ref = $bindable(null),
        class: className,
        ...restProps
    }: CommandPrimitive.EmptyProps = $props();
</script>

<CommandPrimitive.Empty
        {...restProps}
        bind:ref
        class={cn("py-6 text-center text-sm", className)}
        data-slot="command-empty"
/>
