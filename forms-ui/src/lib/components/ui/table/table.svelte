<script lang="ts">
    import type {HTMLTableAttributes} from "svelte/elements";
    import {cn, type WithElementRef} from "$lib/utils.js";

    let {
        ref = $bindable(null),
        class: className,
        children,
        ...restProps
    }: WithElementRef<HTMLTableAttributes> = $props();
</script>

<div class="relative w-full overflow-x-auto" data-slot="table-container">
    <table
            {...restProps}
            bind:this={ref}
            class={cn("w-full caption-bottom text-sm", className)}
            data-slot="table"
    >
        {@render children?.()}
    </table>
</div>
