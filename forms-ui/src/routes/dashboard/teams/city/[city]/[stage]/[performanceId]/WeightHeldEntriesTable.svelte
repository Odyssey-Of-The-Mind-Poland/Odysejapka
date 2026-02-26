<script lang="ts">
    import type { TeamForm, ValidationFailure } from "$lib/utils/form-results";
    import WeightHeldEntryRow from "./WeightHeldEntryRow.svelte";
    import WeightIcon from "@lucide/svelte/icons/weight";

    const { entries = $bindable(), validationErrors = [] } = $props<{
        entries: TeamForm['weightHeldEntries'];
        validationErrors?: ValidationFailure[];
    }>();
</script>

{#if entries.length > 0}
    <div class="flex flex-col gap-3">
        <div class="flex items-center gap-2.5">
            <div class="flex items-center justify-center size-8 rounded-lg bg-amber-500/10">
                <WeightIcon class="size-4 text-amber-600" />
            </div>
            <div>
                <h2 class="text-lg font-semibold tracking-tight">Utrzymany ciężar</h2>
            </div>
        </div>
        <div class="rounded-xl border bg-card shadow-sm overflow-hidden divide-y divide-border">
            {#each entries as weightEntry, i (weightEntry.entry.id)}
                <WeightHeldEntryRow
                    bind:weightEntry={entries[i]}
                    {validationErrors}
                />
            {/each}
        </div>
    </div>
{/if}
