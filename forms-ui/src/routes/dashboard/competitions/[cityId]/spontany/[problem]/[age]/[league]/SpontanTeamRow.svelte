<script lang="ts">
    import {Input} from "$lib/components/ui/input/index.js";
    import * as Select from '$lib/components/ui/select/index.js';

    type SpontanFieldDef = {
        name: string;
        multiplier: number;
        fieldType?: 'MULTIPLIER' | 'EXPRESSION' | 'BOOLEAN';
        expression?: string;
        trueValue?: number;
    };

    type VerbalEntryForm = {
        creative: number;
        common: number;
    };

    type ManualJudgeEntryForm = {
        creativity: number;
        teamwork: number;
    };

    const {
        verbalEntries = $bindable(),
        manualJudgeEntries = $bindable(),
        manualEntries = $bindable(),
        team,
        spontanHour,
        rawSpontan,
        judges,
        type,
        manualFields = [],
        savedSnapshot,
    } = $props<{
        verbalEntries: VerbalEntryForm[];
        manualJudgeEntries: ManualJudgeEntryForm[];
        manualEntries: Record<string, number>;
        team: string;
        spontanHour: string;
        rawSpontan: number | null;
        judges: number[];
        type: 'VERBAL' | 'MANUAL';
        manualFields?: SpontanFieldDef[];
        savedSnapshot: string;
    }>();

    let isDirty = $derived(
        JSON.stringify({v: verbalEntries, mj: manualJudgeEntries, m: manualEntries}) !== savedSnapshot
    );
</script>

<tr class="border-b hover:bg-muted/30 {isDirty ? 'bg-yellow-50 dark:bg-yellow-950/20' : ''}">
    <td class="px-4 py-2 font-medium whitespace-nowrap">
        <div>{team}</div>
        <div class="text-xs text-muted-foreground font-mono">{spontanHour}</div>
    </td>
    {#each manualFields as field}
        <td class="px-1 py-1 text-center border-l">
            {#if field.fieldType === 'BOOLEAN'}
                <Select.Root
                        type="single"
                        value={manualEntries[field.name] ? 'TAK' : 'NIE'}
                        onValueChange={(v) => { manualEntries[field.name] = v === 'TAK' ? 1 : 0; }}
                >
                    <Select.Trigger class="w-20 h-8 text-sm mx-auto">
                        {manualEntries[field.name] ? 'TAK' : 'NIE'}
                    </Select.Trigger>
                    <Select.Content>
                        <Select.Item value="TAK">TAK</Select.Item>
                        <Select.Item value="NIE">NIE</Select.Item>
                    </Select.Content>
                </Select.Root>
            {:else}
                <Input
                        type="number"
                        class="w-20 h-8 text-center text-sm mx-auto"
                        bind:value={manualEntries[field.name]}
                />
            {/if}
        </td>
    {/each}
    {#each judges as j, ji}
        {#if type === 'VERBAL'}
            <td class="px-1 py-1 text-center border-l">
                <Input
                    type="number"
                    class="w-20 h-8 text-center text-sm mx-auto"
                    bind:value={verbalEntries[ji].creative}
                />
            </td>
            <td class="px-1 py-1 text-center">
                <Input
                    type="number"
                    class="w-20 h-8 text-center text-sm mx-auto"
                    bind:value={verbalEntries[ji].common}
                />
            </td>
        {:else}
            <td class="px-1 py-1 text-center border-l">
                <Input
                    type="number"
                    class="w-20 h-8 text-center text-sm mx-auto"
                    bind:value={manualJudgeEntries[ji].creativity}
                />
            </td>
            <td class="px-1 py-1 text-center">
                <Input
                    type="number"
                    class="w-20 h-8 text-center text-sm mx-auto"
                    bind:value={manualJudgeEntries[ji].teamwork}
                />
            </td>
        {/if}
    {/each}
    <td class="px-4 py-2 text-center font-mono tabular-nums font-semibold border-l">
        {rawSpontan != null ? rawSpontan.toFixed(2) : '—'}
    </td>
</tr>
