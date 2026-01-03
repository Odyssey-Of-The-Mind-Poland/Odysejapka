<script lang="ts">
    import {cn, type WithElementRef} from "$lib/utils.js";
    import type {HTMLAttributes} from "svelte/elements";

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
        class={cn("col-start-2 row-span-2 row-start-1 self-start justify-self-end", className)}
        data-slot="card-action"
>
    {@render children?.()}
</div>
