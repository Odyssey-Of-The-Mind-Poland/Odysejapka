<script lang="ts">
	import { Input } from '$lib/components/ui/input/index.js';
	import * as Select from '$lib/components/ui/select/index.js';

	type SpontanFieldDef = {
		name: string;
		multiplier: number;
		fieldType?: 'MULTIPLIER' | 'EXPRESSION' | 'BOOLEAN';
		expression?: string;
		trueValue?: number;
		trueResponse?: string;
		falseResponse?: string;
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
		savedSnapshot
	} = $props<{
		verbalEntries: VerbalEntryForm[];
		manualJudgeEntries: ManualJudgeEntryForm[];
		manualEntries: Record<string, number>;
		team: string;
		spontanHour: string;
		rawSpontan: number | null;
		judges: number[];
		type: 'VERBAL' | 'MANUAL' | 'VERBAL_MANUAL';
		manualFields?: SpontanFieldDef[];
		savedSnapshot: string;
	}>();

	let isDirty = $derived(
		JSON.stringify({ v: verbalEntries, mj: manualJudgeEntries, m: manualEntries }) !== savedSnapshot
	);
</script>

<tr class="hover:bg-muted/30 border-b {isDirty ? 'bg-yellow-50 dark:bg-yellow-950/20' : ''}">
	<!-- Pinned so the team stays visible while the judge columns scroll. A
         sticky cell needs its own opaque background, so it has to repeat the
         row's dirty highlight rather than inheriting it. -->
	<td
		class="sticky left-0 z-10 px-4 py-2 font-medium whitespace-nowrap {isDirty
			? 'bg-yellow-50 dark:bg-yellow-950/20'
			: 'bg-card'}"
	>
		<div>{team}</div>
		<div class="text-muted-foreground font-mono text-xs">{spontanHour}</div>
	</td>
	{#each manualFields as field}
		<td class="border-l px-1 py-1 text-center">
			{#if field.fieldType === 'BOOLEAN'}
				<Select.Root
					type="single"
					value={manualEntries[field.name] ? 'TAK' : 'NIE'}
					onValueChange={(v) => {
						manualEntries[field.name] = v === 'TAK' ? 1 : 0;
					}}
				>
					<Select.Trigger class="mx-auto h-8 w-auto min-w-20 text-sm">
						{manualEntries[field.name]
							? (field.trueResponse ?? 'Tak')
							: (field.falseResponse ?? 'Nie')}
					</Select.Trigger>
					<Select.Content>
						<Select.Item value="TAK">{field.trueResponse ?? 'Tak'}</Select.Item>
						<Select.Item value="NIE">{field.falseResponse ?? 'Nie'}</Select.Item>
					</Select.Content>
				</Select.Root>
			{:else}
				<Input
					type="number"
					class="mx-auto h-8 w-20 text-center text-sm"
					bind:value={manualEntries[field.name]}
				/>
			{/if}
		</td>
	{/each}
	{#each judges as j, ji}
		{#if type === 'VERBAL'}
			<td class="border-l px-1 py-1 text-center">
				<Input
					type="number"
					class="mx-auto h-8 w-20 text-center text-sm"
					bind:value={verbalEntries[ji].creative}
				/>
			</td>
			<td class="px-1 py-1 text-center">
				<Input
					type="number"
					class="mx-auto h-8 w-20 text-center text-sm"
					bind:value={verbalEntries[ji].common}
				/>
			</td>
		{:else}
			<td class="border-l px-1 py-1 text-center">
				<Input
					type="number"
					class="mx-auto h-8 w-20 text-center text-sm"
					bind:value={manualJudgeEntries[ji].creativity}
				/>
			</td>
			<td class="px-1 py-1 text-center">
				<Input
					type="number"
					class="mx-auto h-8 w-20 text-center text-sm"
					bind:value={manualJudgeEntries[ji].teamwork}
				/>
			</td>
		{/if}
	{/each}
	<td class="border-l px-4 py-2 text-center font-mono font-semibold tabular-nums">
		{rawSpontan != null ? rawSpontan.toFixed(2) : '—'}
	</td>
</tr>
