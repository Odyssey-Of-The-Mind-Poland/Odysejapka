<script lang="ts">
    import IconX from "@tabler/icons-svelte/icons/x";

    type Props = {
        startTime: string;
        endTime: string;
        teamName?: string | null;
        slotId: number;
        ondrop: (slotId: number, teamId: number) => void;
        onremove: (slotId: number) => void;
        highlight?: boolean;
        spontanInfo?: string | null;
    };

    let {startTime, endTime, teamName, slotId, ondrop, onremove, highlight = false, spontanInfo = null}: Props = $props();

    let dragOver = $state(false);

    function formatTime(t: string) {
        if (!t) return '';
        if (t.length >= 5) return t.substring(0, 5);
        return t;
    }

    function handleDragOver(e: DragEvent) {
        e.preventDefault();
        if (e.dataTransfer) e.dataTransfer.dropEffect = 'move';
        dragOver = true;
    }

    function handleDragLeave() {
        dragOver = false;
    }

    function handleDrop(e: DragEvent) {
        e.preventDefault();
        dragOver = false;
        const teamId = e.dataTransfer?.getData('application/team-id');
        if (teamId) {
            ondrop(slotId, Number(teamId));
        }
    }
</script>

<div
    class="flex items-center gap-2 rounded-md border px-2 py-1.5 text-sm transition-colors min-h-[40px]
        {dragOver ? 'border-primary bg-primary/10 ring-2 ring-primary/30' : ''}
        {teamName ? 'bg-primary/5 border-primary/30' : 'border-dashed bg-muted/30'}
        {highlight ? 'ring-2 ring-amber-400/50' : ''}"
    role="listitem"
    ondragover={handleDragOver}
    ondragleave={handleDragLeave}
    ondrop={handleDrop}
>
    <span class="text-xs text-muted-foreground whitespace-nowrap font-mono">
        {formatTime(startTime)}-{formatTime(endTime)}
    </span>
    {#if teamName}
        <span class="flex-1 truncate font-medium">{teamName}</span>
        {#if spontanInfo}
            <span class="shrink-0 text-xs text-amber-600 dark:text-amber-400 font-mono">{spontanInfo}</span>
        {/if}
        <button
            class="shrink-0 rounded p-0.5 hover:bg-destructive/10 text-muted-foreground hover:text-destructive transition-colors"
            onclick={() => onremove(slotId)}
            title="Usun"
        >
            <IconX class="size-3.5"/>
        </button>
    {:else}
        <span class="flex-1 text-muted-foreground/50 text-xs italic">Pusty slot</span>
    {/if}
</div>
