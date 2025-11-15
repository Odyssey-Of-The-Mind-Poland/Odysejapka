<script lang="ts">
    import {cn, type WithElementRef} from "$lib/utils.js";
    import type {HTMLThAttributes} from "svelte/elements";

    let {
        ref = $bindable(null),
        class: className,
        children,
        ...restProps
    }: WithElementRef<HTMLThAttributes> = $props();
</script>

<th
        {...restProps}
        bind:this={ref}
        class={cn(
		"text-foreground h-10 whitespace-nowrap bg-clip-padding px-2 text-left align-middle font-medium [&:has([role=checkbox])]:pr-0",
		className
	)}
        data-slot="table-head"
>
    {@render children?.()}
</th>
