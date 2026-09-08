<script lang="ts">
    import type {TeamForm, JudgeType, DtTeamFormEntry, ValidationFailure, Anomaly} from "$lib/utils/form-results";
    import * as Input from "$lib/components/ui/input/index.js";
    import ObjectiveJudgeInput from "./ObjectiveJudgeInput.svelte";
    import SubjectiveJudgeInput from "./SubjectiveJudgeInput.svelte";
    import {Checkbox} from "$lib/components/ui/checkbox";
    import DtEntriesTable from "./DtEntriesTable.svelte";
    import CircleAlertIcon from "@lucide/svelte/icons/circle-alert";
    import TriangleAlertIcon from "@lucide/svelte/icons/triangle-alert";

    const {
        dtEntry = $bindable(),
        allColumns,
        maxJudgeCount,
        isFo = false,
        showNested = true,
        showNoElementColumn = false,
        nestingLevel = 0,
        entryIndex = 0,
        validationErrors = [],
        anomalies = []
    } = $props<{
        dtEntry: DtTeamFormEntry;
        allColumns: Array<{ type: 'DT_A' | 'DT_B', judge: number }>;
        maxJudgeCount: number;
        isFo: boolean;
        showNested?: boolean;
        showNoElementColumn?: boolean;
        nestingLevel?: number;
        entryIndex?: number;
        validationErrors?: ValidationFailure[];
        anomalies?: Anomaly[];
    }>();

    function getColumnLabel(column: { type: 'DT_A' | 'DT_B', judge: number }): string {
        return (column.type === 'DT_A' ? 'Sędzia A' : 'Sędzia B') + column.judge;
    }

    function getDisplayLabel(): string {
        if (nestingLevel === 1) {
            return String.fromCharCode(97 + entryIndex) + ".";
        }
        if (nestingLevel >= 2) {
            return "";
        }
        return (dtEntry.entry.sortIndex + 1) + ".";
    }

    let previousNoElement = $state(dtEntry.noElement);

    let entryErrors = $derived(
        validationErrors.filter((e: ValidationFailure) => e.entryId === dtEntry.entry.id)
    );
    let hasError = $derived(entryErrors.length > 0);

    let entryAnomalies = $derived(
        anomalies.filter((a: Anomaly) => a.entryId === dtEntry.entry.id)
    );
    let hasAnomaly = $derived(entryAnomalies.length > 0);

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
    <div class="px-3 py-3 bg-muted/30 lg:px-5">
        <div class="flex items-baseline gap-2">
            {#if getDisplayLabel()}
                <span class="text-sm font-bold text-foreground tabular-nums">{getDisplayLabel()}</span>
            {/if}
            <span class="font-semibold text-foreground">{dtEntry.entry.name}</span>
        </div>
    </div>

    {#if hasNestedEntries}
        <div class="border-l-2 border-primary/20 ml-2 lg:ml-5">
            <DtEntriesTable
                entries={dtEntry.nestedEntries}
                isFo={isFo}
                showHeader={false}
                nested={true}
                nestingLevel={nestingLevel + 1}
                parentAllColumns={allColumns}
                parentMaxJudgeCount={maxJudgeCount}
                parentShowNoElementColumn={showNoElementColumn}
                {validationErrors}
                {anomalies}
            />
        </div>
    {/if}
{:else}
    <!-- Scoring entry row -->
    <div class="flex flex-col gap-3 px-3 py-3 transition-colors hover:bg-muted/30 group lg:flex-row lg:items-start lg:gap-4 lg:px-5 {hasError ? 'border-l-3 border-l-destructive bg-destructive/5' : hasAnomaly ? 'border-l-3 border-l-amber-500 bg-amber-500/5' : ''}">
        <!-- Left: Index + Name -->
        <div class="min-w-0 lg:flex-1 lg:pt-1.5">
            <div class="flex items-start gap-2">
                {#if getDisplayLabel()}
                    <span class="text-sm text-muted-foreground font-mono tabular-nums shrink-0 pt-0.5">
                        {getDisplayLabel()}
                    </span>
                {/if}
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
                    {#if hasAnomaly}
                        <div class="flex items-center gap-1">
                            <TriangleAlertIcon class="size-3 text-amber-600 shrink-0" />
                            <span class="text-xs text-amber-600">{entryAnomalies[0].message}</span>
                        </div>
                    {/if}
                </div>
            </div>
        </div>

        <!-- Right: Judge inputs. Below `lg` these wrap into a 2-up grid; four
             88px fields plus the 16rem no-element column never fit 390px. -->
        <div class="grid grid-cols-2 gap-2 lg:flex lg:items-start lg:shrink-0">
            {#each allColumns as column}
                {@const isEnabled = isColumnEnabled(column)}
                {@const objectiveBucket = dtEntry.entry.scoring?.objectiveBucket}
                {@const subjectiveRange = dtEntry.entry.scoring?.subjectiveRange}
                {@const isObjective = dtEntry.entry.scoring?.scoringType === 'OBJECTIVE' && objectiveBucket}
                {@const isSubjective = dtEntry.entry.scoring?.scoringType === 'SUBJECTIVE' && subjectiveRange}
                {#if isObjective && objectiveBucket}
                    <ObjectiveJudgeInput
                        objectiveBucketName={objectiveBucket}
                        label={getColumnLabel(column)}
                        bind:value={dtEntry.results[column.type][column.judge]}
                        disabled={!isEnabled || dtEntry.noElement}
                    />
                {:else if isSubjective && subjectiveRange}
                    <SubjectiveJudgeInput
                        subjectiveRangeName={subjectiveRange}
                        isFo={isFo}
                        label={getColumnLabel(column)}
                        bind:value={dtEntry.results[column.type][column.judge]}
                        disabled={!isEnabled || dtEntry.noElement}
                    />
                {:else if !isEnabled}
                    <div class="hidden lg:block lg:w-[5.5rem]"></div>
                {/if}
            {/each}

            {#if showNoElementColumn}
                <div class="col-span-2 flex flex-col items-center justify-center gap-1.5 border-t border-border pt-2 lg:col-span-1 lg:w-[16rem] lg:ml-2 lg:border-t-0 lg:border-l lg:pl-2 lg:pt-0">
                    {#if dtEntry.entry.scoring?.noElementEnabled}
                        {@const entryId = dtEntry.entry.id ?? 0}
                        <div class="flex items-center gap-2">
                            <Checkbox
                                id="no-element-{entryId}"
                                bind:checked={dtEntry.noElement}
                            />
                            <!-- The column header carries this label from `lg` up. -->
                            <label for="no-element-{entryId}" class="text-xs text-muted-foreground lg:hidden">
                                Brak elementu
                            </label>
                        </div>
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
        <div class="border-l-2 border-primary/20 ml-2 lg:ml-5">
            <DtEntriesTable
                entries={dtEntry.nestedEntries!}
                isFo={isFo}
                showHeader={false}
                nested={true}
                nestingLevel={nestingLevel + 1}
                parentAllColumns={allColumns}
                parentMaxJudgeCount={maxJudgeCount}
                parentShowNoElementColumn={showNoElementColumn}
                {validationErrors}
                {anomalies}
            />
        </div>
    {/if}
{/if}
