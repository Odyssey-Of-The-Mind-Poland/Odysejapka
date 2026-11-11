<script lang="ts">
	import { apiFetch } from '$lib/api';
	import { Input } from '$lib/components/ui/input/index.js';
	import { createQuery } from '@tanstack/svelte-query';

	type SpontanTestResult = {
		success: boolean;
		result: number | null;
	};

	const { expresion = $bindable() } = $props<{
		expresion: string | undefined;
	}>();

	let value = $state(0);

	const resultsQuery = createQuery(() => ({
		queryKey: ['spontanTester', expresion, value],
		queryFn: () =>
			apiFetch<SpontanTestResult>('/api/v1/spontan/calculate', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ formula: expresion, value: value })
			}),
		enabled: !!expresion?.trim()
	}));
</script>

<div class="flex items-center gap-2 pl-5">
	Testowa wartość:
	<Input bind:value placeholder="0" class="w-20 font-mono" />
	Wynik:
	{#if resultsQuery.data?.success}
		{resultsQuery.data?.result}
	{:else}
		<div class="text-red-500">Błędna Formuła</div>
	{/if}
</div>
