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

    let rangeDisplay = $derived.by(() => {
        if (!rangesQuery.data) return '';
        const range = rangesQuery.data.find(r => r.name === subjectiveRangeName);
        if (!range) return '';
        if (isFo) {
            return `${range.foRanges.min} – ${range.foRanges.max} pkt`;
        } else {
            const vals = range.frValues.values;
            if (vals.length <= 4) return vals.join(', ') + ' pkt';
            return `${vals[0]} – ${vals[vals.length - 1]} pkt`;
        }
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
                {#each availableValues as rangeValue}
                    <Select.Item value={String(rangeValue)} label={String(rangeValue)}>
                        {rangeValue}
                    </Select.Item>
                {/each}
            </Select.Group>
        </Select.Content>
    </Select.Root>
</div>
