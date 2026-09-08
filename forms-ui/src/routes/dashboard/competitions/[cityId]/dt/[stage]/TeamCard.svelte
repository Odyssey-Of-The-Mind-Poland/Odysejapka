<script lang="ts">
    import {Badge} from "$lib/components/ui/badge/index.js";
    import BugIcon from "@lucide/svelte/icons/bug";
    import ObsuwaBadge from "./ObsuwaBadge.svelte";
    import FormStateBadge from "./FormStateBadge.svelte";

    /**
     * Card rendering of one team row, used below `md` where the eleven-column
     * table would only be reachable by horizontal scrolling. Shows the same
     * fields, stacked: identity and status on top, times next, scores in a grid.
     */
    let {team, showWeight = false, onclick}: {
        team: {
            team: string;
            ranatra: boolean;
            formState: string;
            formStateLabel: string;
            expectedTime: string;
            actualTime: string | null;
            rawDt: number | null;
            rawStyle: number | null;
            rawPenalty: number | null;
            rawWeight: number | null;
            rawSpontan: number | null;
            rawTotal: number | null;
        };
        showWeight?: boolean;
        onclick: () => void;
    } = $props();

    let scores = $derived([
        {label: 'DT', value: team.rawDt},
        {label: 'Styl', value: team.rawStyle},
        {label: 'Karne', value: team.rawPenalty},
        {label: 'Spontan', value: team.rawSpontan},
        ...(showWeight ? [{label: 'Waga', value: team.rawWeight}] : []),
    ]);
</script>

<button
        type="button"
        class="flex w-full flex-col gap-3 p-4 text-left transition-colors hover:bg-muted/50 active:bg-muted"
        {onclick}
>
    <div class="flex items-start justify-between gap-2">
        <div class="flex min-w-0 items-center gap-1.5">
            <span class="truncate font-medium">{team.team}</span>
            {#if team.ranatra}
                <Badge variant="default" class="shrink-0 px-1.5 py-0 text-[10px]">
                    <BugIcon class="mr-0.5 size-3"/>
                    R
                </Badge>
            {/if}
        </div>
        <div class="shrink-0">
            <FormStateBadge formState={team.formState} formStateLabel={team.formStateLabel}/>
        </div>
    </div>

    <div class="flex flex-wrap items-center gap-x-2 gap-y-1 text-sm">
        <span class="font-mono tabular-nums">{team.expectedTime}</span>
        <span class="text-muted-foreground">→</span>
        <span class="font-mono tabular-nums">
            {#if team.actualTime}{team.actualTime}{:else}<span class="text-muted-foreground">—</span>{/if}
        </span>
        <ObsuwaBadge expectedTime={team.expectedTime} actualTime={team.actualTime}/>
    </div>

    <div class="grid grid-cols-3 gap-2 border-t pt-3">
        {#each scores as score (score.label)}
            <div class="flex flex-col gap-0.5">
                <span class="text-[10px] uppercase tracking-wide text-muted-foreground">{score.label}</span>
                <span class="font-mono text-sm tabular-nums">
                    {#if score.value != null}{score.value.toFixed(2)}{:else}<span class="text-muted-foreground">—</span>{/if}
                </span>
            </div>
        {/each}
        <div class="flex flex-col gap-0.5">
            <span class="text-[10px] uppercase tracking-wide text-muted-foreground">Suma</span>
            <span class="font-mono text-sm font-semibold tabular-nums">
                {#if team.rawTotal != null}{team.rawTotal.toFixed(2)}{:else}<span class="text-muted-foreground">—</span>{/if}
            </span>
        </div>
    </div>
</button>
