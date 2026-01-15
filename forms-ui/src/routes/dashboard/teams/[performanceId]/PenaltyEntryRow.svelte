<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import type {TeamForm} from "$lib/utils/form-results";
    import RangePenaltyInput from "./RangePenaltyInput.svelte";
    import DiscretePenaltyInput from "./DiscretePenaltyInput.svelte";
    import SinglePenaltyInput from "./SinglePenaltyInput.svelte";
    import ZeroBalsaPenaltyInput from "./ZeroBalsaPenaltyInput.svelte";

    let {
        penaltyEntry = $bindable(),
    } = $props<{
        penaltyEntry: TeamForm['penaltyEntries'][0];
    }>();
</script>

<Table.Row>
    <Table.Cell class="font-medium">
        <div class="flex flex-col">
            <div class="font-extralight text-sm">
                {penaltyEntry.entry.sortIndex}
            </div>
            <div>
                {penaltyEntry.entry.name}
            </div>
        </div>
    </Table.Cell>
    <Table.Cell>{penaltyEntry.entry.penaltyType || 'N/A'}</Table.Cell>
    <Table.Cell>
        {#if penaltyEntry.entry.penaltyType === 'RANGE' && penaltyEntry.entry.penaltyRange}
            <RangePenaltyInput
                bind:value={penaltyEntry.result}
                min={penaltyEntry.entry.penaltyRange.min}
                max={penaltyEntry.entry.penaltyRange.max}
            />
        {:else if penaltyEntry.entry.penaltyType === 'DISCRETE' && penaltyEntry.entry.penaltyDiscrete}
            <DiscretePenaltyInput
                bind:value={penaltyEntry.result}
                values={penaltyEntry.entry.penaltyDiscrete.values}
            />
        {:else if penaltyEntry.entry.penaltyType === 'SINGLE' && penaltyEntry.entry.penaltySingle}
            <SinglePenaltyInput
                bind:value={penaltyEntry.result}
                singleValue={penaltyEntry.entry.penaltySingle.value}
            />
        {:else if penaltyEntry.entry.penaltyType === 'ZERO_BALSA'}
            <ZeroBalsaPenaltyInput
                bind:checked={penaltyEntry.zeroBalsa}
            />
        {/if}
    </Table.Cell>
</Table.Row>

