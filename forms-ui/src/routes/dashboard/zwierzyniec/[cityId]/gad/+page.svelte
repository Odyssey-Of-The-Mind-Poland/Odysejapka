<script lang="ts">
	import { page } from '$app/state';
	import {
		runGad,
		stopGad,
		getGadStatus,
		type GadRequest,
		type PunctationCells
	} from '$lib/zwierzyniec';
	import { createOdysejaQuery } from '$lib/queries';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Spinner } from '$lib/components/ui/spinner';
	import { toast } from 'svelte-sonner';
	import { onMount, onDestroy } from 'svelte';

	let cityId = $derived(Number(page.params.cityId));

	let gadQuery = $derived(
		createOdysejaQuery<GadRequest>({
			queryKey: ['gad', String(cityId)],
			path: `/api/v1/gad?cityId=${cityId}`
		})
	);

	let data = $state<GadRequest>({
		templatesFolderId: '',
		destinationFolderId: '',
		zspId: '',
		problemPunctuationCells: {}
	});

	let gadProgress = $state<{ status: string; progress: number; logs: { logTime: string; message: string }[] }>({
		status: 'STOPPED',
		progress: 100,
		logs: []
	});

	let intervalId: ReturnType<typeof setInterval> | null = null;

	$effect(() => {
		const d = gadQuery.data;
		if (d) {
			data = {
				templatesFolderId: d.templatesFolderId ?? '',
				destinationFolderId: d.destinationFolderId ?? '',
				zspId: d.zspId ?? '',
				problemPunctuationCells: d.problemPunctuationCells ?? {}
			};
		}
	});

	function updateCell(index: number, field: keyof PunctationCells, value: string) {
		const existing = data.problemPunctuationCells[index] ?? {};
		const cell = { ...existing, [field]: value };
		data = {
			...data,
			problemPunctuationCells: { ...data.problemPunctuationCells, [index]: cell }
		};
	}

	function startGad() {
		runGad(cityId, data)
			.then(() => {
				toast.success('Rozpoczęto generowanie arkuszy');
				gadProgress = { status: 'RUNNING', progress: 0, logs: [] };
			})
			.catch((e) => toast.error(e instanceof Error ? e.message : 'Błąd'));
	}

	function stopGadRun() {
		stopGad(cityId)
			.then(() => {
				toast.success('Zatrzymano generowanie');
				gadProgress = { status: 'STOPPED', progress: 100, logs: [] };
			})
			.catch((e) => toast.error(e instanceof Error ? e.message : 'Błąd'));
	}

	onMount(() => {
		getGadStatus(cityId).then((p) => {
			gadProgress = { ...p, logs: p.logs ?? [] };
		});
		intervalId = setInterval(() => {
			if (cityId && gadProgress.status === 'RUNNING') {
				getGadStatus(cityId).then((p) => {
					gadProgress = { ...p, logs: p.logs ?? [] };
				});
			}
		}, 5000);
	});

	onDestroy(() => {
		if (intervalId) clearInterval(intervalId);
	});
</script>

<div class="flex flex-col gap-5">
	<h2 class="text-xl font-semibold">GAD</h2>

	{#if gadQuery.isPending}
		<div class="flex items-center gap-3 py-4">
			<Spinner size="sm" />
			<span class="text-muted-foreground">Ładowanie...</span>
		</div>
	{:else if gadQuery.error}
		<div class="rounded-lg border border-destructive/30 bg-destructive/5 p-4">
			<p class="text-sm text-destructive">{String(gadQuery.error)}</p>
		</div>
	{:else}
		<div class="flex flex-col gap-3 max-w-2xl">
			<div class="grid gap-2">
				<Label>Folder z bazowymi arkuszami</Label>
				<Input bind:value={data.templatesFolderId} placeholder="Folder z bazowymi arkuszami" />
			</div>
			<div class="grid gap-2">
				<Label>Folder dla wygenerowanych arkuszy</Label>
				<Input bind:value={data.destinationFolderId} placeholder="Folder dla wygenerowanych arkuszy" />
			</div>
			<div class="grid gap-2">
				<Label>Link do ZSP</Label>
				<Input bind:value={data.zspId} placeholder="Link do ZSP" />
			</div>
		</div>

		{#each [1, 2, 3, 4, 5] as i}
			<div class="flex flex-col gap-2">
				<h3 class="font-medium">Problem {i}</h3>
				<div class="flex flex-wrap gap-3">
					<Input
						placeholder="DT"
						value={data.problemPunctuationCells[i]?.dt ?? ''}
						oninput={(e) => updateCell(i, 'dt', e.currentTarget.value)}
						class="w-24"
					/>
					<Input
						placeholder="Styl"
						value={data.problemPunctuationCells[i]?.style ?? ''}
						oninput={(e) => updateCell(i, 'style', e.currentTarget.value)}
						class="w-24"
					/>
					<Input
						placeholder="Karne"
						value={data.problemPunctuationCells[i]?.penalty ?? ''}
						oninput={(e) => updateCell(i, 'penalty', e.currentTarget.value)}
						class="w-24"
					/>
					{#if i === 4}
						<Input
							placeholder="Balsa"
							value={data.problemPunctuationCells[i]?.balsa ?? ''}
							oninput={(e) => updateCell(i, 'balsa', e.currentTarget.value)}
							class="w-24"
						/>
					{/if}
					<Input
						placeholder="Anomalia"
						value={data.problemPunctuationCells[i]?.anomaly ?? ''}
						oninput={(e) => updateCell(i, 'anomaly', e.currentTarget.value)}
						class="w-24"
					/>
					<Input
						placeholder="Weryfikacja Anomali"
						value={data.problemPunctuationCells[i]?.anomalyVerify ?? ''}
						oninput={(e) => updateCell(i, 'anomalyVerify', e.currentTarget.value)}
						class="w-32"
					/>
					<Input
						placeholder="Czas występu"
						value={data.problemPunctuationCells[i]?.actualPerformanceStartTime ?? ''}
						oninput={(e) => updateCell(i, 'actualPerformanceStartTime', e.currentTarget.value)}
						class="w-32"
					/>
				</div>
			</div>
		{/each}

		<div class="flex flex-col gap-3">
			{#if gadProgress.status === 'STOPPED'}
				<Button onclick={startGad}>Generuj arkusze</Button>
			{:else}
				<Button variant="destructive" onclick={stopGadRun}>Zatrzymaj generowanie</Button>
			{/if}
			{#if gadProgress.status !== 'STOPPED' || (gadProgress.logs && gadProgress.logs.length > 0)}
				<div class="w-full">
					<div class="h-2 rounded-full bg-muted overflow-hidden">
						<div
							class="h-full bg-primary transition-all"
							style="width: {gadProgress.progress}%"
						></div>
					</div>
				</div>
			{/if}
		</div>

		{#if gadProgress.logs && gadProgress.logs.length > 0}
			<div class="flex flex-col p-4 bg-muted rounded-lg font-mono text-sm max-h-48 overflow-y-auto">
				{#each gadProgress.logs as log}
					<div class="flex gap-3">
						<span class="text-muted-foreground shrink-0">{log.logTime}</span>
						<span>> {log.message}</span>
					</div>
				{/each}
			</div>
		{/if}
	{/if}
</div>
