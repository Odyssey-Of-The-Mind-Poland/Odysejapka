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

<thead
        {...restProps}
        bind:this={ref}
        class={cn("[&_tr]:border-b", className)}
        data-slot="table-header"
>
{@render children?.()}
</thead>
