<script lang="ts">
    import * as Select from '$lib/components/ui/select/index.js';
    import {createOdysejaQuery} from "$lib/queries";
    import type {ScoringData} from "./types";

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
        <div class="group relative w-full">
            <label
                    class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
                    for="bucket-select"
            >
                <span class="inline-flex bg-background px-1">Możliwe wartości punktowe</span>
            </label>
            <Select.Root
                    type="single"
                    bind:value={scoring.objectiveBucket}
            >
                <Select.Trigger class="w-full pt-5" id="bucket-select">
                    {selectedBucket?.displayName || "Wybierz buckety"}
                </Select.Trigger>
                <Select.Content>
                    <Select.Group>
                        {#each bucketsQuery.data as bucket}
                            <Select.Item value={bucket.name} label={bucket.displayName}>
                                {bucket.displayName}
                            </Select.Item>
                        {/each}
                    </Select.Group>
                </Select.Content>
            </Select.Root>
        </div>
    </div>
{/if}

