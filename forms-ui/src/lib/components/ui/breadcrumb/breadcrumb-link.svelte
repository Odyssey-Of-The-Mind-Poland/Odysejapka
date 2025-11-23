<script lang="ts">
    export let href: string | undefined = undefined;
    export let className: string = "transition-colors hover:text-foreground";
</script>

<a class={className} {href}>
    <slot/>
</a>
