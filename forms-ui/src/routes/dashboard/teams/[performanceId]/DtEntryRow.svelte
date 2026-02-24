<script lang="ts">
    import type {TeamForm, JudgeType, DtTeamFormEntry, ValidationFailure} from "$lib/utils/form-results";
    import * as Input from "$lib/components/ui/input/index.js";
    import ObjectiveJudgeInput from "./ObjectiveJudgeInput.svelte";
    import SubjectiveJudgeInput from "./SubjectiveJudgeInput.svelte";
    import {Checkbox} from "$lib/components/ui/checkbox";
    import DtEntriesTable from "./DtEntriesTable.svelte";
    import CircleAlertIcon from "@lucide/svelte/icons/circle-alert";

    const {
        dtEntry = $bindable(),
        allColumns,
        maxJudgeCount,
        isFo = false,
        showNested = true,
        showNoElementColumn = false,
        validationErrors = []
    } = $props<{
        dtEntry: DtTeamFormEntry;
        allColumns: Array<{ type: 'DT_A' | 'DT_B', judge: number }>;
        maxJudgeCount: number;
        isFo: boolean;
        showNested?: boolean;
        showNoElementColumn?: boolean;
        validationErrors?: ValidationFailure[];
    }>();

    let previousNoElement = $state(dtEntry.noElement);

    let entryErrors = $derived(
        validationErrors.filter(e => e.entryId === dtEntry.entry.id)
    );
    let hasError = $derived(entryErrors.length > 0);

    function isColumnEnabled(
        column: { type: 'DT_A' | 'DT_B', judge: number }
    ): boolean {
        if (column.judge > maxJudgeCount) {
            return false;
        }

        const judgesConfig = dtEntry.entry.scoring?.judges;
        if (!judgesConfig) {
            return true;
        }

        if (judgesConfig === 'A') {
            return column.type === 'DT_A';
        }
        if (judgesConfig === 'B') {
            return column.type === 'DT_B';
        }
        if (judgesConfig === 'A_PLUS_B') {
            return true;
        }

        return true;
    }

    let isSection = $derived(
        dtEntry.entry.type === 'SECTION' || dtEntry.entry.type === 'SCORING_GROUP'
    );
    let hasScoring = $derived(!!dtEntry.entry.scoring);
    let hasNestedEntries = $derived(
        showNested && dtEntry.nestedEntries && dtEntry.nestedEntries.length > 0 && isSection
    );

    $effect(() => {
        if (dtEntry.noElement && !previousNoElement) {
            Object.keys(dtEntry.results).forEach((judgeType) => {
                const judgeMap = dtEntry.results[judgeType as JudgeType];
                Object.keys(judgeMap).forEach((judge) => {
                    judgeMap[Number(judge)] = 0;
                });
            });
        }
        previousNoElement = dtEntry.noElement;
    });
</script>

{#if isSection && !hasScoring}
    <!-- Section header row -->
    <div class="px-5 py-3 bg-muted/30">
        <div class="flex items-baseline gap-2">
            <span class="text-sm font-bold text-foreground tabular-nums">{dtEntry.entry.sortIndex}.</span>
            <span class="font-semibold text-foreground">{dtEntry.entry.name}</span>
        </div>
    </div>

    {#if hasNestedEntries}
        <div class="border-l-2 border-primary/20 ml-5">
            <DtEntriesTable
                entries={dtEntry.nestedEntries!}
                isFo={isFo}
                showHeader={false}
                nested={true}
                parentAllColumns={allColumns}
                parentMaxJudgeCount={maxJudgeCount}
                parentShowNoElementColumn={showNoElementColumn}
                {validationErrors}
            />
        </div>
    {/if}
{:else}
    <!-- Scoring entry row -->
    <div class="flex items-start gap-4 px-5 py-3 transition-colors hover:bg-muted/30 group {hasError ? 'border-l-3 border-l-destructive bg-destructive/5' : ''}">
        <!-- Left: Index + Name -->
        <div class="flex-1 min-w-0 pt-1.5">
            <div class="flex items-start gap-2">
                <span class="text-sm text-muted-foreground font-mono tabular-nums shrink-0 pt-0.5">
                    {dtEntry.entry.sortIndex}.
                </span>
                <div class="flex flex-col gap-0.5">
                    <span class="text-sm font-medium text-foreground">
                        {dtEntry.entry.name}
                    </span>
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
        <div class="flex items-start gap-2 shrink-0">
            {#each allColumns as column}
                {@const isEnabled = isColumnEnabled(column)}
                {@const objectiveBucket = dtEntry.entry.scoring?.objectiveBucket}
                {@const subjectiveRange = dtEntry.entry.scoring?.subjectiveRange}
                {@const isObjective = dtEntry.entry.scoring?.scoringType === 'OBJECTIVE' && objectiveBucket}
                {@const isSubjective = dtEntry.entry.scoring?.scoringType === 'SUBJECTIVE' && subjectiveRange}
                {#if isObjective && objectiveBucket}
                    <ObjectiveJudgeInput
                        objectiveBucketName={objectiveBucket}
                        bind:value={dtEntry.results[column.type][column.judge]}
                        disabled={!isEnabled || dtEntry.noElement}
                    />
                {:else if isSubjective && subjectiveRange}
                    <SubjectiveJudgeInput
                        subjectiveRangeName={subjectiveRange}
                        isFo={isFo}
                        bind:value={dtEntry.results[column.type][column.judge]}
                        disabled={!isEnabled || dtEntry.noElement}
                    />
                {:else if !isEnabled}
                    <div class="w-[5.5rem]"></div>
                {/if}
            {/each}

            {#if showNoElementColumn}
                <div class="w-[16rem] ml-2 pl-2 border-l border-border flex flex-col items-center justify-center gap-1.5">
                    {#if dtEntry.entry.scoring?.noElementEnabled}
                        {@const entryId = dtEntry.entry.id ?? 0}
                        <Checkbox
                            id="no-element-{entryId}"
                            bind:checked={dtEntry.noElement}
                        />
                        {#if dtEntry.noElement}
                            <Input.Input
                                type="text"
                                bind:value={dtEntry.noElementComment}
                                class="w-full h-7 text-xs"
                                placeholder="Komentarz..."
                                maxlength={100}
                            />
                        {/if}
                    {/if}
                </div>
            {/if}
        </div>
    </div>

    {#if hasNestedEntries}
        <div class="border-l-2 border-primary/20 ml-5">
            <DtEntriesTable
                entries={dtEntry.nestedEntries!}
                isFo={isFo}
                showHeader={false}
                nested={true}
                parentAllColumns={allColumns}
                parentMaxJudgeCount={maxJudgeCount}
                parentShowNoElementColumn={showNoElementColumn}
                {validationErrors}
            />
        </div>
    {/if}
{/if}
