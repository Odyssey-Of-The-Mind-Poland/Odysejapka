<script lang="ts">
    import type { TeamForm } from "$lib/utils/form-results";
    import PenaltyEntryRow from "./PenaltyEntryRow.svelte";
    import TriangleAlertIcon from "@lucide/svelte/icons/triangle-alert";

    const { entries = $bindable() } = $props<{
        entries: TeamForm['penaltyEntries'];
    }>();
</script>

{#if entries.length > 0}
    <div class="flex flex-col gap-3">
        <div class="flex items-center gap-2.5">
            <div class="flex items-center justify-center size-8 rounded-lg bg-destructive/10">
                <TriangleAlertIcon class="size-4 text-destructive" />
            </div>
            <div>
                <h2 class="text-lg font-semibold tracking-tight">Karne</h2>
                <p class="text-xs text-muted-foreground">{entries.length} {entries.length === 1 ? 'wpis' : 'wpisów'}</p>
            </div>
        </div>
        <div class="rounded-xl border bg-card shadow-sm overflow-hidden divide-y divide-border">
            {#each entries as penaltyEntry, i (penaltyEntry.entry.id)}
                <PenaltyEntryRow
                    bind:penaltyEntry={entries[i]}
                />
            {/each}
        </div>
    </div>
{/if}
