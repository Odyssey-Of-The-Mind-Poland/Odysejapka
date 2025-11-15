<script lang="ts">
    import {Select as SelectPrimitive} from "bits-ui";
    import {cn} from "$lib/utils.js";
    import type {ComponentProps} from "svelte";

    let {
        ref = $bindable(null),
        class: className,
        children,
        ...restProps
    }: ComponentProps<typeof SelectPrimitive.GroupHeading> = $props();
</script>

<SelectPrimitive.GroupHeading
        {...restProps}
        bind:ref
        class={cn("text-muted-foreground px-2 py-1.5 text-xs", className)}
        data-slot="select-group-heading"
>
    {@render children?.()}
</SelectPrimitive.GroupHeading>
