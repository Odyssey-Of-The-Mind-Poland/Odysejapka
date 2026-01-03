<script lang="ts">
    import {cn, type WithElementRef} from "$lib/utils/index.js";
    import type {HTMLAttributes} from "svelte/elements";

    let {
        ref = $bindable(null),
        class: className,
        children,
        ...restProps
    }: WithElementRef<HTMLAttributes<HTMLSpanElement>> = $props();
</script>

<span
        {...restProps}
        bind:this={ref}
        class={cn("text-muted-foreground ml-auto text-xs tracking-widest", className)}
        data-slot="command-shortcut"
>
	{@render children?.()}
</span>
