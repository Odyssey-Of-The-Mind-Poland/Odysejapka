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
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Spinner } from '$lib/components/ui/spinner';
	import * as Card from '$lib/components/ui/card';
	import * as Collapsible from '$lib/components/ui/collapsible';
	import ProgressPanel from '$lib/components/ProgressPanel.svelte';
	import IconChevronDown from '@tabler/icons-svelte/icons/chevron-down';
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
	<Card.Root>
		<Card.Header>
			<Card.Title>GAD</Card.Title>
			<Card.Description>Generator Arkuszy z Danymi — generowanie arkuszy oceniania</Card.Description>
		</Card.Header>
		<Card.Content>
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
				<div class="flex flex-col gap-6">
					<div class="grid gap-4 sm:grid-cols-2 max-w-2xl">
						<div class="space-y-2">
							<Label>Folder z bazowymi arkuszami</Label>
							<Input bind:value={data.templatesFolderId} placeholder="ID folderu" />
						</div>
						<div class="space-y-2">
							<Label>Folder dla wygenerowanych arkuszy</Label>
							<Input bind:value={data.destinationFolderId} placeholder="ID folderu" />
						</div>
						<div class="space-y-2 sm:col-span-2">
							<Label>Link do ZSP</Label>
							<Input bind:value={data.zspId} placeholder="Link do ZSP" />
						</div>
					</div>

					<div class="flex flex-col gap-3">
						<h3 class="text-sm font-medium text-muted-foreground">Konfiguracja problemów</h3>
						{#each [1, 2, 3, 4, 5] as i}
							<Collapsible.Root class="rounded-lg border">
								<Collapsible.Trigger class="flex w-full items-center justify-between px-4 py-3 text-sm font-medium hover:bg-accent/50 transition-colors [&[data-state=open]>svg]:rotate-180">
									Problem {i}
									<IconChevronDown class="size-4 transition-transform duration-200" />
								</Collapsible.Trigger>
								<Collapsible.Content>
									<div class="grid gap-4 px-4 pb-4 pt-2 sm:grid-cols-2">
										<div class="space-y-1.5">
											<Label class="text-xs">DT</Label>
											<Input
												value={data.problemPunctuationCells[i]?.dt ?? ''}
												oninput={(e) => updateCell(i, 'dt', e.currentTarget.value)}
												placeholder="Komórka DT"
											/>
										</div>
										<div class="space-y-1.5">
											<Label class="text-xs">Styl</Label>
											<Input
												value={data.problemPunctuationCells[i]?.style ?? ''}
												oninput={(e) => updateCell(i, 'style', e.currentTarget.value)}
												placeholder="Komórka stylu"
											/>
										</div>
										<div class="space-y-1.5">
											<Label class="text-xs">Karne</Label>
											<Input
												value={data.problemPunctuationCells[i]?.penalty ?? ''}
												oninput={(e) => updateCell(i, 'penalty', e.currentTarget.value)}
												placeholder="Komórka kar"
											/>
										</div>
										{#if i === 4}
											<div class="space-y-1.5">
												<Label class="text-xs">Balsa</Label>
												<Input
													value={data.problemPunctuationCells[i]?.balsa ?? ''}
													oninput={(e) => updateCell(i, 'balsa', e.currentTarget.value)}
													placeholder="Komórka balsy"
												/>
											</div>
										{/if}
										<div class="space-y-1.5">
											<Label class="text-xs">Anomalia</Label>
											<Input
												value={data.problemPunctuationCells[i]?.anomaly ?? ''}
												oninput={(e) => updateCell(i, 'anomaly', e.currentTarget.value)}
												placeholder="Komórka anomalii"
											/>
										</div>
										<div class="space-y-1.5">
											<Label class="text-xs">Weryfikacja anomalii</Label>
											<Input
												value={data.problemPunctuationCells[i]?.anomalyVerify ?? ''}
												oninput={(e) => updateCell(i, 'anomalyVerify', e.currentTarget.value)}
												placeholder="Komórka weryfikacji"
											/>
										</div>
										<div class="space-y-1.5">
											<Label class="text-xs">Czas występu</Label>
											<Input
												value={data.problemPunctuationCells[i]?.actualPerformanceStartTime ?? ''}
												oninput={(e) => updateCell(i, 'actualPerformanceStartTime', e.currentTarget.value)}
												placeholder="Komórka czasu"
											/>
										</div>
									</div>
								</Collapsible.Content>
							</Collapsible.Root>
						{/each}
					</div>
				</div>
			{/if}
		</Card.Content>
	</Card.Root>

	{#if !gadQuery.isPending && !gadQuery.error}
		<ProgressPanel
			status={gadProgress.status}
			progress={gadProgress.progress}
			logs={gadProgress.logs}
			onstart={startGad}
			onstop={stopGadRun}
			startLabel="Generuj arkusze"
			stopLabel="Zatrzymaj generowanie"
		/>
	{/if}
</div>
