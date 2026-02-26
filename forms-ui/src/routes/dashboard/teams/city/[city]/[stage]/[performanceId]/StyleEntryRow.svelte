<script lang="ts">
    import type {TeamForm, JudgeType} from "$lib/utils/form-results";
    import PredefinedStyleInput from "./PredefinedStyleInput.svelte";
    import FreeTeamChoiceInput from "./FreeTeamChoiceInput.svelte";
    import StyleNameInput from "./StyleNameInput.svelte";

    let {
        styleEntry = $bindable(),
        allJudgeColumns,
    } = $props<{
        styleEntry: TeamForm['styleEntries'][0];
        allJudgeColumns: number[];
    }>();
</script>

<div class="flex items-center gap-4 px-5 py-3 transition-colors hover:bg-muted/30 group">
    <!-- Left: Index + Name -->
    <div class="flex-1 min-w-0">
        <div class="flex items-start gap-2">
            <span class="text-xs text-muted-foreground font-mono tabular-nums shrink-0 pt-0.5">
                {styleEntry.entry.sortIndex}.
            </span>
            <div class="flex flex-col gap-1">
                <span class="text-sm font-medium text-foreground">
                    {styleEntry.entry.name}
                </span>
                {#if styleEntry.entry.styleType === 'FREE_TEAM_CHOICE'}
                    <StyleNameInput bind:value={styleEntry.styleName} />
                {/if}
            </div>
        </div>
    </div>

    <!-- Right: Judge inputs -->
    <div class="flex items-center gap-4 shrink-0">
        {#each allJudgeColumns as judge}
            {#if styleEntry.entry.styleType === 'PREDEFINED'}
                <PredefinedStyleInput
                    bind:value={styleEntry.results.STYLE[judge]}
                />
            {:else if styleEntry.entry.styleType === 'FREE_TEAM_CHOICE'}
                <FreeTeamChoiceInput
                    bind:value={styleEntry.results.STYLE[judge]}
                />
            {/if}
        {/each}
    </div>
</div>
