<script lang="ts">
    import { Checkbox } from "$lib/components/ui/checkbox/index.js";
    import { Label } from "$lib/components/ui/label/index.js";

    interface Props {
        value: boolean;
    }

    let {value = $bindable()}: Props = $props();
    
    let checkboxId = `noElementEnabled-${Math.random().toString(36).substr(2, 9)}`;
</script>

<div class="flex items-center gap-3 w-full">
    <Checkbox id={checkboxId} bind:checked={value} />
    <Label for={checkboxId} class="text-sm font-normal cursor-pointer">
        Brak elementu
    </Label>
</div>

