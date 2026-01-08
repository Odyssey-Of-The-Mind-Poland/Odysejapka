<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import {Button} from '$lib/components/ui/button';
    import Trash2Icon from '@lucide/svelte/icons/trash-2';
    import type {FormEntryType} from "./types";
    import ScoringTypeSelect from "./ScoringTypeSelect.svelte";
    import EntryNameInput from "./EntryNameInput.svelte";

    interface Props {
        entry: FormEntryType;
        onRemove?: () => void;
    }

    let {entry = $bindable(), onRemove}: Props = $props();
</script>

<Card.Root>
    <div class="flex items-center gap-2 p-2">
        {#if entry.scoring}
            <ScoringTypeSelect bind:value={entry.scoring.scoringType} />
        {/if}
        <EntryNameInput bind:value={entry.name} id={entry.id} />
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

