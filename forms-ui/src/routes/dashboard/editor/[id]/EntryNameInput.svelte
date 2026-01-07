<script lang="ts">
    import {Input} from '$lib/components/ui/input';

    interface Props {
        value: string;
        id?: string | number | null;
    }

    let {value = $bindable(), id}: Props = $props();
</script>

<div class="group relative flex-1">
    <label
            class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
            for={id?.toString() || "0"}
    >
        <span class="inline-flex bg-background px-1">Nazwa</span>
    </label>
    <Input bind:value={value} class="w-full dark:bg-background pt-5" placeholder=" "
           type="text"/>
</div>

