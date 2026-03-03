<script lang="ts">
    import type {TeamForm, JudgeType, ValidationFailure} from "$lib/utils/form-results";
    import PredefinedStyleInput from "./PredefinedStyleInput.svelte";
    import FreeTeamChoiceInput from "./FreeTeamChoiceInput.svelte";
    import StyleNameInput from "./StyleNameInput.svelte";
    import CircleAlertIcon from "@lucide/svelte/icons/circle-alert";

    let {
        styleEntry = $bindable(),
        allJudgeColumns,
        validationErrors = [],
    } = $props<{
        styleEntry: TeamForm['styleEntries'][0];
        allJudgeColumns: number[];
        validationErrors?: ValidationFailure[];
    }>();

    let entryErrors = $derived(
        validationErrors.filter((e: ValidationFailure) => e.entryId === styleEntry.entry.id)
    );
    let hasError = $derived(entryErrors.length > 0);
</script>

<div class="flex items-center gap-4 px-5 py-3 transition-colors hover:bg-muted/30 group {hasError ? 'border-l-3 border-l-destructive bg-destructive/5' : ''}">
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
                {#if hasError}
                    <div class="flex items-center gap-1">
                        <CircleAlertIcon class="size-3 text-destructive shrink-0" />
                        <span class="text-xs text-destructive">{entryErrors[0].message}</span>
                    </div>
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
