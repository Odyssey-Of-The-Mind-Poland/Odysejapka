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

<tbody
        {...restProps}
        bind:this={ref}
        class={cn("[&_tr:last-child]:border-0", className)}
        data-slot="table-body"
>
{@render children?.()}
</tbody>
