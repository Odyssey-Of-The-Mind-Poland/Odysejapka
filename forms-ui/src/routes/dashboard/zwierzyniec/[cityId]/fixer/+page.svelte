<script lang="ts">
	import { page } from '$app/state';
	import { runFixer, stopFixer, getFixerStatus, type ReplacementCell } from '$lib/zwierzyniec';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { toast } from 'svelte-sonner';
	import { onMount, onDestroy } from 'svelte';

	let cityId = $derived(Number(page.params.cityId));

	let pattern = $state('');
	let folderId = $state('');
	let fixerProgress = $state<{ status: string; progress: number; logs: { logTime: string; message: string }[] }>({
		status: 'STOPPED',
		progress: 100,
		logs: []
	});

	let cells = $state<ReplacementCell[]>([{ sheetName: '', cell: '', value: '' }]);
	let intervalId: ReturnType<typeof setInterval> | null = null;

	function addCell() {
		cells = [...cells, { sheetName: '', cell: '', value: '' }];
	}

	function removeCell(index: number) {
		cells = cells.filter((_, i) => i !== index);
	}

	function fix() {
		runFixer(cityId, { folderId, pattern, cells })
			.then(() => {
				toast.success('Rozpoczęto naprawianie arkuszy');
				fixerProgress = { status: 'RUNNING', progress: 0, logs: [] };
			})
			.catch((e) => toast.error(e instanceof Error ? e.message : 'Błąd'));
	}

	function stopFixing() {
		stopFixer(cityId)
			.then(() => {
				toast.success('Zatrzymano naprawianie');
				fixerProgress = { status: 'STOPPED', progress: 100, logs: [] };
			})
			.catch((e) => toast.error(e instanceof Error ? e.message : 'Błąd'));
	}

	onMount(() => {
		getFixerStatus(cityId).then((p) => {
			fixerProgress = { ...p, logs: p.logs ?? [] };
		});
		intervalId = setInterval(() => {
			if (cityId && fixerProgress.status === 'RUNNING') {
				getFixerStatus(cityId).then((p) => {
					fixerProgress = { ...p, logs: p.logs ?? [] };
				});
			}
		}, 5000);
	});

	onDestroy(() => {
		if (intervalId) clearInterval(intervalId);
	});
</script>

<div class="flex flex-col gap-5">
	<h2 class="text-xl font-semibold">Fixer</h2>

	<div class="flex flex-col gap-3 max-w-2xl">
		<div class="grid gap-2">
			<Label for="pattern">Arkusz pattern</Label>
			<Input id="pattern" bind:value={pattern} placeholder="Arkusz pattern" />
		</div>
		<div class="grid gap-2">
			<Label for="folderId">Folder z arkuszami</Label>
			<Input id="folderId" bind:value={folderId} placeholder="Folder z arkuszami" />
		</div>

		<div class="flex flex-col gap-2">
			<Label>Komórki do podmiany</Label>
			{#each cells as cell, i}
				<div class="flex gap-2 items-center">
					<Input bind:value={cell.sheetName} placeholder="Nazwa arkusza" class="flex-1" />
					<Input bind:value={cell.cell} placeholder="Komórka" class="flex-1" />
					<Input bind:value={cell.value} placeholder="Podmieniona wartość" class="flex-1" />
					<Button variant="destructive" size="icon" onclick={() => removeCell(i)}>
						Usuń
					</Button>
				</div>
			{/each}
			<Button variant="outline" onclick={addCell}>Dodaj komórkę</Button>
		</div>
	</div>

	<div class="flex flex-col gap-3">
		{#if fixerProgress.status === 'STOPPED'}
			<Button onclick={fix}>Napraw</Button>
		{:else}
			<Button variant="destructive" onclick={stopFixing}>Zatrzymaj</Button>
		{/if}
		{#if fixerProgress.status !== 'STOPPED' || (fixerProgress.logs && fixerProgress.logs.length > 0)}
			<div class="w-full">
				<div class="h-2 rounded-full bg-muted overflow-hidden">
					<div
						class="h-full bg-primary transition-all"
						style="width: {fixerProgress.progress}%"
					></div>
				</div>
			</div>
		{/if}
	</div>

	{#if fixerProgress.logs && fixerProgress.logs.length > 0}
		<div class="flex flex-col p-4 bg-muted rounded-lg font-mono text-sm max-h-48 overflow-y-auto">
			{#each fixerProgress.logs as log}
				<div class="flex gap-3">
					<span class="text-muted-foreground shrink-0">{log.logTime}</span>
					<span>> {log.message}</span>
				</div>
			{/each}
		</div>
	{/if}
</div>
