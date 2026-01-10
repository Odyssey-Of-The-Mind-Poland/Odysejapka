<script lang="ts">
    import * as Select from '$lib/components/ui/select/index.js';
    import {createOdysejaQuery} from "$lib/queries";
    import type {ScoringData} from "./types";
    import SelectWithLabel from '$lib/components/form/SelectWithLabel.svelte';

    interface Props {
        scoring: ScoringData;
    }

    type ObjectiveBucket = {
        name: string;
        displayName: string;
        bucketsDisplay: string;
    };

    let {scoring = $bindable()}: Props = $props();

    let bucketsQuery = createOdysejaQuery<ObjectiveBucket[]>({
        queryKey: ['objective-buckets'],
        path: '/api/v1/form/objective-buckets',
    });

    let selectedBucket = $derived.by(() => {
        if (!scoring.objectiveBucket || !bucketsQuery.data) return null;
        return bucketsQuery.data.find(b => b.name === scoring.objectiveBucket) || null;
    });
</script>

{#if bucketsQuery.data && scoring}
    <div class="flex items-center gap-4 w-full">
        <SelectWithLabel 
            label="Możliwe wartości punktowe" 
            bind:value={scoring.objectiveBucket} 
            flexClass="w-full"
            triggerContent={() => selectedBucket?.displayName || "Wybierz buckety"}
        >
            <Select.Group>
                {#each bucketsQuery.data as bucket}
                    <Select.Item value={bucket.name} label={bucket.displayName}>
                        {bucket.displayName}
                    </Select.Item>
                {/each}
            </Select.Group>
        </SelectWithLabel>
    </div>
{/if}

