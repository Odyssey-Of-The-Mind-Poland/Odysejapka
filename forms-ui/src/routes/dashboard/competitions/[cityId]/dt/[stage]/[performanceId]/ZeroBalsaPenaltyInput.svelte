<script lang="ts">
    import { Checkbox } from "$lib/components/ui/checkbox/index.js";

    let {checked = $bindable(), disabled = false} = $props<{
        checked?: boolean;
        disabled?: boolean;
    }>();
</script>

<div class="flex flex-col gap-1">
    <span class="text-[10px] text-muted-foreground leading-none">Wartość</span>
    <div class="w-full h-10 flex items-center justify-center lg:w-[5.5rem] lg:h-8">
        <Checkbox bind:checked disabled={disabled} />
    </div>
</div>
