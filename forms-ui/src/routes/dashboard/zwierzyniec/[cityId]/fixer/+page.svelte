<script lang="ts">
	import { page } from '$app/state';
	import { runFixer, stopFixer, getFixerStatus, type ReplacementCell } from '$lib/zwierzyniec';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import * as Card from '$lib/components/ui/card';
	import ProgressPanel from '$lib/components/ProgressPanel.svelte';
	import IconTrash from '@tabler/icons-svelte/icons/trash';
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
	<Card.Root>
		<Card.Header>
			<Card.Title>Fixer</Card.Title>
			<Card.Description>Masowa podmiana wartości w komórkach arkuszy</Card.Description>
		</Card.Header>
		<Card.Content>
			<div class="flex flex-col gap-6">
				<div class="grid gap-4 sm:grid-cols-2 max-w-2xl">
					<div class="space-y-2">
						<Label for="pattern">Arkusz pattern</Label>
						<Input id="pattern" bind:value={pattern} placeholder="Wzorzec nazwy arkusza" />
					</div>
					<div class="space-y-2">
						<Label for="folderId">Folder z arkuszami</Label>
						<Input id="folderId" bind:value={folderId} placeholder="ID folderu" />
					</div>
				</div>

				<div class="flex flex-col gap-3">
					<Label>Komórki do podmiany</Label>
					<div class="rounded-lg border overflow-hidden">
						<div class="grid grid-cols-[1fr_1fr_1fr_auto] gap-px bg-muted text-xs font-medium text-muted-foreground">
							<div class="bg-background px-3 py-2">Arkusz</div>
							<div class="bg-background px-3 py-2">Komórka</div>
							<div class="bg-background px-3 py-2">Wartość</div>
							<div class="bg-background px-3 py-2 w-10"></div>
						</div>
						{#each cells as cell, i}
							<div class="grid grid-cols-[1fr_1fr_1fr_auto] gap-px bg-muted">
								<div class="bg-background px-2 py-1.5">
									<Input bind:value={cell.sheetName} placeholder="Nazwa arkusza" class="border-0 shadow-none h-8" />
								</div>
								<div class="bg-background px-2 py-1.5">
									<Input bind:value={cell.cell} placeholder="np. A1" class="border-0 shadow-none h-8" />
								</div>
								<div class="bg-background px-2 py-1.5">
									<Input bind:value={cell.value} placeholder="Nowa wartość" class="border-0 shadow-none h-8" />
								</div>
								<div class="bg-background px-2 py-1.5 flex items-center justify-center w-10">
									<Button variant="ghost" size="icon" class="h-8 w-8 text-muted-foreground hover:text-destructive" onclick={() => removeCell(i)}>
										<IconTrash class="size-4" />
									</Button>
								</div>
							</div>
						{/each}
					</div>
					<Button variant="outline" size="sm" onclick={addCell} class="w-fit">Dodaj komórkę</Button>
				</div>
			</div>
		</Card.Content>
	</Card.Root>

	<ProgressPanel
		status={fixerProgress.status}
		progress={fixerProgress.progress}
		logs={fixerProgress.logs}
		onstart={fix}
		onstop={stopFixing}
		startLabel="Napraw"
		stopLabel="Zatrzymaj"
	/>
</div>
