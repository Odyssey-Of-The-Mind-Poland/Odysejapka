<script lang="ts">
    export let className: string = "font-normal text-foreground";
</script>

<span aria-current="page" class={className}>
  <slot/>
</span>
