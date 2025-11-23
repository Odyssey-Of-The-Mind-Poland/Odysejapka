<script lang="ts">
    export let className: string = "";
</script>

<nav aria-label="Breadcrumb" class={className}>
    <slot/>
</nav>
