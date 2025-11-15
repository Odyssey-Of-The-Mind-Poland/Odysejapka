<script lang="ts">
    import {cn, type WithElementRef} from "$lib/utils.js";
    import type {HTMLAttributes} from "svelte/elements";

    let {
        ref = $bindable(null),
        class: className,
        children,
        ...restProps
    }: WithElementRef<HTMLAttributes<HTMLTableSectionElement>> = $props();
</script>

<tfoot
        {...restProps}
        bind:this={ref}
        class={cn("bg-muted/50 border-t font-medium [&>tr]:last:border-b-0", className)}
        data-slot="table-footer"
>
{@render children?.()}
</tfoot>
