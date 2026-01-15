<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
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

<Table.Row>
    <Table.Cell>
        <div class="flex flex-col">
            <div class="font-extralight text-sm">
                {styleEntry.entry.sortIndex}
            </div>

        </div>
    </Table.Cell>

    <Table.Cell>
        {#if styleEntry.entry.styleType === 'FREE_TEAM_CHOICE'}
            <StyleNameInput
                bind:value={styleEntry.styleName}
            />
        {:else}
            {styleEntry.entry.name}
        {/if}
    </Table.Cell>
    {#each allJudgeColumns as judge}
        <Table.Cell>
            {#if styleEntry.entry.styleType === 'PREDEFINED'}
                <PredefinedStyleInput
                    bind:value={styleEntry.results.STYLE[judge]}
                />
            {:else if styleEntry.entry.styleType === 'FREE_TEAM_CHOICE'}
                <FreeTeamChoiceInput
                    bind:value={styleEntry.results.STYLE[judge]}
                />
            {/if}
        </Table.Cell>
    {/each}
</Table.Row>

