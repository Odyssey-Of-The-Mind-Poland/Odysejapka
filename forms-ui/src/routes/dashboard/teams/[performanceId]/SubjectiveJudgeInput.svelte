<script lang="ts">
    import * as Select from "$lib/components/ui/select/index.js";
    import {createOdysejaQuery} from "$lib/queries";

    type SubjectiveRange = {
        name: string;
        displayName: string;
        foDisplay: string;
        frDisplay: string;
        foRanges: {
            min: number;
            max: number;
        };
        frValues: {
            values: number[];
        };
    };

    interface Props {
        subjectiveRangeName: string;
        isFo: boolean;
        value?: number | string | null;
        disabled?: boolean;
    }

    let { subjectiveRangeName, isFo, value = $bindable(), disabled = false }: Props = $props();

    let rangesQuery = createOdysejaQuery<SubjectiveRange[]>({
        queryKey: ['subjective-ranges'],
        path: '/api/v1/form/subjective-ranges',
    });

    let availableValues = $derived.by(() => {
        if (!rangesQuery.data) return [];
        const range = rangesQuery.data.find(r => r.name === subjectiveRangeName);
        if (!range) return [];

        if (isFo) {
            const values: number[] = [];
            for (let i = range.foRanges.min; i <= range.foRanges.max; i++) {
                values.push(i);
            }
            return values;
        } else {
            return range.frValues.values;
        }
    });

    let stringValue = $derived(
        value !== null && value !== undefined ? String(value) : undefined
    );

    function handleValueChange(newValue: string | undefined) {
        value = newValue ? Number(newValue) : null;
    }
</script>

<Select.Root
    type="single"
    value={stringValue}
    onValueChange={handleValueChange}
    disabled={disabled}
>
    <Select.Trigger class="w-24" disabled={disabled}>
        {stringValue ?? 'Wybierz'}
    </Select.Trigger>
    <Select.Content>
        <Select.Group>
            {#each availableValues as rangeValue}
                <Select.Item value={String(rangeValue)} label={String(rangeValue)}>
                    {rangeValue}
                </Select.Item>
            {/each}
        </Select.Group>
    </Select.Content>
</Select.Root>

