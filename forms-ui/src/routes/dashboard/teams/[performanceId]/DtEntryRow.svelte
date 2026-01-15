<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import * as Input from "$lib/components/ui/input/index.js";
    import type {TeamForm, JudgeType} from "$lib/utils/form-results";
    import ObjectiveJudgeInput from "./ObjectiveJudgeInput.svelte";
    import SubjectiveJudgeInput from "./SubjectiveJudgeInput.svelte";
    import {Checkbox} from "$lib/components/ui/checkbox";

    const {
        dtEntry = $bindable(),
        allColumns,
        maxJudgeCount,
        isFo = false
    } = $props<{
        dtEntry: TeamForm['dtEntries'][0];
        allColumns: Array<{ type: 'DT_A' | 'DT_B', judge: number }>;
        maxJudgeCount: number;
        isFo: boolean;
    }>();

    let previousNoElement = $state(dtEntry.noElement);

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

<Table.Row>
    <Table.Cell>
        <div class="flex flex-col">
            <div class="font-extralight text-sm">
                {dtEntry.entry.sortIndex}
            </div>
            <div class="font-medium">
                {dtEntry.entry.name}
            </div>
        </div>
    </Table.Cell>
    {#each allColumns as column}
        {@const isEnabled = isColumnEnabled(column)}
        {@const objectiveBucket = dtEntry.entry.scoring?.objectiveBucket}
        {@const subjectiveRange = dtEntry.entry.scoring?.subjectiveRange}
        {@const isObjective = dtEntry.entry.scoring?.scoringType === 'OBJECTIVE' && objectiveBucket}
        {@const isSubjective = dtEntry.entry.scoring?.scoringType === 'SUBJECTIVE' && subjectiveRange}
        <Table.Cell>
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
            {:else}
                <Input.Input
                        id="judge-{column.type}-{dtEntry.entry.id}-{column.judge}"
                        type="number"
                        bind:value={dtEntry.results[column.type][column.judge]}
                        class="w-24"
                        disabled={!isEnabled || dtEntry.noElement}
                />
            {/if}
        </Table.Cell>
    {/each}
    <Table.Cell>
        {#if dtEntry.entry.scoring.noElementEnabled}
            {@const entryId = dtEntry.entry.id ?? 0}
            <Checkbox 
                id="no-element-{entryId}"
                bind:checked={dtEntry.noElement}
            />
        {/if}
    </Table.Cell>
</Table.Row>

