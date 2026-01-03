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

<div
        {...restProps}
        bind:this={ref}
        class={cn("leading-none font-semibold", className)}
        data-slot="card-title"
>
    {@render children?.()}
</div>
