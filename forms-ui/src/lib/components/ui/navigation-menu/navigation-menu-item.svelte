<script lang="ts">
    import {NavigationMenu as NavigationMenuPrimitive} from "bits-ui";
    import {cn} from "$lib/utils.js";

    let {
        ref = $bindable(null),
        class: className,
        ...restProps
    }: NavigationMenuPrimitive.ItemProps = $props();
</script>

<NavigationMenuPrimitive.Item
        {...restProps}
        bind:ref
        class={cn("relative", className)}
        data-slot="navigation-menu-item"
/>
