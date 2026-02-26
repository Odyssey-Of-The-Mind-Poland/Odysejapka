<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";
    import {createOdysejaQuery} from "$lib/queries";

    type ObjectiveBucket = {
        name: string;
        displayName: string;
        bucketsDisplay: string;
        buckets: number[];
    };

    interface Props {
        objectiveBucketName: string;
        value?: number | string | null;
        disabled?: boolean;
    }

    let { objectiveBucketName, value = $bindable(), disabled = false }: Props = $props();

    let bucketsQuery = createOdysejaQuery<ObjectiveBucket[]>({
        queryKey: ['objective-buckets'],
        path: '/api/v1/form/objective-buckets',
    });

    let availableValues = $derived.by(() => {
        if (!bucketsQuery.data) return [];
        const bucket = bucketsQuery.data.find(b => b.name === objectiveBucketName);
        return bucket?.buckets || [];
    });

    let stringValue = $derived(
        value !== null && value !== undefined ? String(value) : undefined
    );

    function handleValueChange(newValue: string | undefined) {
        value = newValue ? Number(newValue) : null;
    }
</script>

<div class="flex flex-col gap-1">
    <span class="text-[10px] text-muted-foreground leading-none">Punkty</span>
    <Select.Root
        type="single"
        value={stringValue}
        onValueChange={handleValueChange}
        disabled={disabled}
    >
        <Select.Trigger class="w-[5.5rem] h-8 text-sm" disabled={disabled}>
            {stringValue ?? 'Punkty'}
        </Select.Trigger>
        <Select.Content>
            <Select.Group>
                {#each availableValues as bucketValue}
                    <Select.Item value={String(bucketValue)} label={String(bucketValue)}>
                        {bucketValue}
                    </Select.Item>
                {/each}
            </Select.Group>
        </Select.Content>
    </Select.Root>
</div>
