<script lang="ts">
    import { Checkbox } from "$lib/components/ui/checkbox/index.js";

    let {checked = $bindable(), disabled = false} = $props<{
        checked?: boolean;
        disabled?: boolean;
    }>();
</script>

<div class="flex flex-col gap-1">
    <span class="text-[10px] text-muted-foreground leading-none">Wartość</span>
    <div class="w-[5.5rem] h-8 flex items-center justify-center">
        <Checkbox bind:checked disabled={disabled} />
    </div>
</div>
