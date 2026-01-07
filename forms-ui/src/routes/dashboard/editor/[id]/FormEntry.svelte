<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {Input} from '$lib/components/ui/input';
    import {Button} from '$lib/components/ui/button';
    import Trash2Icon from '@lucide/svelte/icons/trash-2';
    import type {FormEntryType} from "./types";

    interface Props {
        entry: FormEntryType;
        onRemove?: () => void;
    }

    let {entry = $bindable(), onRemove}: Props = $props();
</script>

<Card.Root>
    <div class="flex items-center gap-2">
        <div class="group relative flex-1 p-2">
            <label
                    class="origin-start absolute top-1/2 block -translate-y-1/2 cursor-text px-2 text-sm text-muted-foreground transition-all group-focus-within:pointer-events-none group-focus-within:top-0 group-focus-within:cursor-default group-focus-within:text-xs group-focus-within:font-medium group-focus-within:text-foreground has-[+input:not(:placeholder-shown)]:pointer-events-none has-[+input:not(:placeholder-shown)]:top-0 has-[+input:not(:placeholder-shown)]:cursor-default has-[+input:not(:placeholder-shown)]:text-xs has-[+input:not(:placeholder-shown)]:font-medium has-[+input:not(:placeholder-shown)]:text-foreground"
                    for={entry.id?.toString() || "0"}
            >
                <span class="inline-flex bg-background px-1">Nazwa</span>
            </label>
            <Input bind:value={entry.name} class="w-full dark:bg-background" placeholder=" "
                   type="text"/>
        </div>
        {#if onRemove}
            <Button
                    variant="ghost"
                    size="icon"
                    onclick={onRemove}
                    class="shrink-0"
                    title="Usuń wpis"
            >
                <Trash2Icon class="h-4 w-4 text-destructive"/>
            </Button>
        {/if}
    </div>
</Card.Root>