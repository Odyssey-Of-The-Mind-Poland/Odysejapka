<script lang="ts">
    import {Badge} from "$lib/components/ui/badge/index.js";

    const { expectedTime, actualTime } = $props<{
        expectedTime: string;
        actualTime: string | null;
    }>();

    function parseTime(time: string): number | null {
        const match = time.match(/^(\d{1,2}):(\d{2})$/);
        if (!match) return null;
        return parseInt(match[1]) * 60 + parseInt(match[2]);
    }

    let obsuwa = $derived.by(() => {
        if (!actualTime) return null;
        const expectedMin = parseTime(expectedTime);
        const actualMin = parseTime(actualTime);
        if (expectedMin === null || actualMin === null) return null;
        return actualMin - expectedMin;
    });
</script>

{#if obsuwa !== null}
    {#if obsuwa > 0}
        <Badge variant="destructive" class="font-mono tabular-nums">+{obsuwa} min</Badge>
    {:else if obsuwa === 0}
        <Badge variant="secondary" class="font-mono tabular-nums">0 min</Badge>
    {:else}
        <Badge class="font-mono tabular-nums bg-green-600 text-white hover:bg-green-700">{obsuwa} min</Badge>
    {/if}
{:else}
    <span class="text-muted-foreground">—</span>
{/if}
