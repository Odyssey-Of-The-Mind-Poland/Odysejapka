<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {Button} from '$lib/components/ui/button';
    import Trash2Icon from '@lucide/svelte/icons/trash-2';
    import type {FormEntryType} from "./types";
    import {formatSortIndex} from "./sortIndexUtils";
    import EntryNameInput from "./EntryNameInput.svelte";

    interface Props {
        entry: FormEntryType;
        onRemove?: () => void;
        parentIndex?: string;
    }

    let {entry = $bindable(), onRemove, parentIndex}: Props = $props();

    let displayIndex = $derived(formatSortIndex(entry, parentIndex));
</script>

<Card.Root>
    <div class="flex flex-col gap-4 p-2">
        <div class="flex items-center justify-between">
            <span class="font-semibold text-gray-700 min-w-[2rem]">{displayIndex}.</span>
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
        <div class="flex items-center gap-2">
            <EntryNameInput bind:value={entry.name} id={entry.id} />
        </div>
    </div>
</Card.Root>

