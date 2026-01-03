<script lang="ts">
    import {cn} from "$lib/utils/index.js";
    import {Command as CommandPrimitive} from "bits-ui";

    export type CommandRootApi = CommandPrimitive.Root;

    let {
        api = $bindable(null),
        ref = $bindable(null),
        value = $bindable(""),
        class: className,
        ...restProps
    }: CommandPrimitive.RootProps & {
        api?: CommandRootApi | null;
    } = $props();
</script>

<CommandPrimitive.Root
        {...restProps}
        bind:ref
        bind:this={api}
        bind:value
        class={cn(
		"bg-popover text-popover-foreground flex h-full w-full flex-col overflow-hidden rounded-md",
		className
	)}
        data-slot="command"
/>
