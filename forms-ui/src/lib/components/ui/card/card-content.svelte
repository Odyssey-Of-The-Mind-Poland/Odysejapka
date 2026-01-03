<script lang="ts">
    import type {HTMLAttributes} from "svelte/elements";
    import {cn, type WithElementRef} from "$lib/utils.js";

    let {
        ref = $bindable(null),
        class: className,
        children,
        ...restProps
    }: WithElementRef<HTMLAttributes<HTMLDivElement>> = $props();
</script>

<div {...restProps} bind:this={ref} class={cn("px-6", className)} data-slot="card-content">
    {@render children?.()}
</div>
