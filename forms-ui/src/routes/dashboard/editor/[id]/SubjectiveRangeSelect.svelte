<script lang="ts">
    import * as Select from '$lib/components/ui/select/index.js';
    import {createOdysejaQuery} from "$lib/queries";
    import type {ScoringData} from "./types";

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

{#if rangesQuery.data && scoring && scoring.subjectiveRange}
    <div class="flex items-center gap-2">
        <div class="group relative w-[180px]">
            <label
                    class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
                    for="range-select"
            >
                <span class="inline-flex bg-background px-1">Zakres</span>
            </label>
            <Select.Root 
                type="single" 
                bind:value={scoring.subjectiveRange}
            >
                <Select.Trigger class="w-[180px] pt-5" id="range-select">
                    {selectedRange?.displayName || "Wybierz zakres"}
                </Select.Trigger>
                <Select.Content>
                    <Select.Group>
                        {#each rangesQuery.data as range}
                            <Select.Item value={range.name} label={range.displayName}>
                                {range.displayName}
                            </Select.Item>
                        {/each}
                    </Select.Group>
                </Select.Content>
            </Select.Root>
        </div>
        {#if selectedRange}
            <div class="group relative w-[200px]">
                <label
                        class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
                        for="fr-select"
                >
                    <span class="inline-flex bg-background px-1">FR</span>
                </label>
                <Select.Root type="single" disabled={true}>
                    <Select.Trigger class="w-[200px] pt-5" id="fr-select">
                        {selectedRange.frDisplay}
                    </Select.Trigger>
                </Select.Root>
            </div>
            <div class="group relative w-[200px]">
                <label
                        class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
                        for="fo-select"
                >
                    <span class="inline-flex bg-background px-1">FO</span>
                </label>
                <Select.Root type="single" disabled>
                    <Select.Trigger class="w-[200px] pt-5" id="fo-select">
                        {selectedRange.foDisplay}
                    </Select.Trigger>
                </Select.Root>
            </div>
        {/if}
    </div>
{/if}

