<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {Input} from '$lib/components/ui/input';
    import type {FormEntryType} from "./types";

    let {entry = $bindable<FormEntryType>()} = $props();
</script>

<Card.Root>
    <div class="group relative w-full p-2">
        <label
                class="origin-start absolute top-1/2 block -translate-y-1/2 cursor-text px-2 text-sm text-muted-foreground transition-all group-focus-within:pointer-events-none group-focus-within:top-0 group-focus-within:cursor-default group-focus-within:text-xs group-focus-within:font-medium group-focus-within:text-foreground has-[+input:not(:placeholder-shown)]:pointer-events-none has-[+input:not(:placeholder-shown)]:top-0 has-[+input:not(:placeholder-shown)]:cursor-default has-[+input:not(:placeholder-shown)]:text-xs has-[+input:not(:placeholder-shown)]:font-medium has-[+input:not(:placeholder-shown)]:text-foreground"
                for={entry.id}
        >
            <span class="inline-flex bg-background px-1">Nazwa</span>
        </label>
        <Input bind:value={entry.name} class="w-full dark:bg-background" placeholder=" "
               type="text"/>
    </div>
</Card.Root>