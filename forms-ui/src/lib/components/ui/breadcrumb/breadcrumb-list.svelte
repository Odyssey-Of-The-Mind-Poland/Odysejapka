<script lang="ts">
    export let className: string = "flex flex-wrap items-center gap-1.5 text-sm text-muted-foreground sm:gap-2.5";
</script>

<ol class={className}>
    <slot/>
</ol>
