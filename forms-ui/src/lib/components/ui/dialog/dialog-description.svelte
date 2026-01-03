<script lang="ts">
    import {Dialog as DialogPrimitive} from "bits-ui";
    import {cn} from "$lib/utils/index.js";

    let {
        ref = $bindable(null),
        class: className,
        ...restProps
    }: DialogPrimitive.DescriptionProps = $props();
</script>

<DialogPrimitive.Description
        {...restProps}
        bind:ref
        class={cn("text-muted-foreground text-sm", className)}
        data-slot="dialog-description"
/>
