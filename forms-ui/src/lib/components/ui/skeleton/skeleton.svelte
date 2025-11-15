<script lang="ts">
    import {cn, type WithElementRef, type WithoutChildren} from "$lib/utils.js";
    import type {HTMLAttributes} from "svelte/elements";

    let {
        ref = $bindable(null),
        class: className,
        ...restProps
    }: WithoutChildren<WithElementRef<HTMLAttributes<HTMLDivElement>>> = $props();
</script>

<div
        {...restProps}
        bind:this={ref}
        class={cn("bg-accent animate-pulse rounded-md", className)}
        data-slot="skeleton"
></div>
