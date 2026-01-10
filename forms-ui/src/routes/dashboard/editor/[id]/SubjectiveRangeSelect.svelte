<script lang="ts">
    import * as Select from '$lib/components/ui/select/index.js';
    import {createOdysejaQuery} from "$lib/queries";
    import type {ScoringData} from "./types";
    import SelectWithLabel from '$lib/components/form/SelectWithLabel.svelte';

    interface Props {
        scoring: ScoringData;
    }

    type SubjectiveRange = {
        name: string;
        displayName: string;
        foDisplay: string;
        frDisplay: string;
    };

    let {scoring = $bindable()}: Props = $props();

    let rangesQuery = createOdysejaQuery<SubjectiveRange[]>({
        queryKey: ['subjective-ranges'],
        path: '/api/v1/form/subjective-ranges',
    });

    let selectedRange = $derived.by(() => {
        if (!scoring.subjectiveRange || !rangesQuery.data) return null;
        return rangesQuery.data.find(r => r.name === scoring.subjectiveRange) || null;
    });
</script>

{#if rangesQuery.data && scoring}
    <div class="flex items-center gap-4 w-full">
        <SelectWithLabel 
            label="Zakres" 
            bind:value={scoring.subjectiveRange} 
            flexClass="w-full"
            triggerContent={() => selectedRange?.displayName || "Wybierz zakres"}
        >
            <Select.Group>
                {#each rangesQuery.data as range}
                    <Select.Item value={range.name} label={range.displayName}>
                        {range.displayName}
                    </Select.Item>
                {/each}
            </Select.Group>
        </SelectWithLabel>
        {#if selectedRange}
            <SelectWithLabel 
                label="FR" 
                value={selectedRange.frDisplay}
                disabled={true}
                flexClass="w-full"
                triggerContent={() => selectedRange.frDisplay}
            >
                <Select.Group>
                    <Select.Item value={selectedRange.frDisplay} label={selectedRange.frDisplay}>
                        {selectedRange.frDisplay}
                    </Select.Item>
                </Select.Group>
            </SelectWithLabel>
            <SelectWithLabel 
                label="FO" 
                value={selectedRange.foDisplay}
                disabled={true}
                flexClass="w-full"
                triggerContent={() => selectedRange.foDisplay}
            >
                <Select.Group>
                    <Select.Item value={selectedRange.foDisplay} label={selectedRange.foDisplay}>
                        {selectedRange.foDisplay}
                    </Select.Item>
                </Select.Group>
            </SelectWithLabel>
        {/if}
    </div>
{/if}

