<script lang="ts">
    import {cn, type WithElementRef} from "$lib/utils.js";
    import type {HTMLTdAttributes} from "svelte/elements";

    let {
        ref = $bindable(null),
        class: className,
        children,
        ...restProps
    }: WithElementRef<HTMLTdAttributes> = $props();
</script>

<td
        {...restProps}
        bind:this={ref}
        class={cn(
		"whitespace-nowrap bg-clip-padding p-2 align-middle [&:has([role=checkbox])]:pr-0",
		className
	)}
        data-slot="table-cell"
>
    {@render children?.()}
</td>
