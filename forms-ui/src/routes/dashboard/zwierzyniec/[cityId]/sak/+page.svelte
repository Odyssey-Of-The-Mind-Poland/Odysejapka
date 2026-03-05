<script lang="ts">
	import { page } from '$app/state';
	import { runSak, stopSak, getSakStatus } from '$lib/zwierzyniec';
	import { createOdysejaQuery } from '$lib/queries';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Spinner } from '$lib/components/ui/spinner';
	import * as Card from '$lib/components/ui/card';
	import ProgressPanel from '$lib/components/ProgressPanel.svelte';
	import { toast } from 'svelte-sonner';
	import { onMount, onDestroy } from 'svelte';

	let cityId = $derived(Number(page.params.cityId));

	let sakQuery = $derived(
		createOdysejaQuery<{ templatesFolderId: string; zspId: string }>({
			queryKey: ['sak', String(cityId)],
			path: `/api/v1/sak?cityId=${cityId}`
		})
	);

	let folderId = $state('');
	let zspId = $state('');
	let sakProgress = $state<{ status: string; progress: number; logs: { logTime: string; message: string }[] }>({
		status: 'STOPPED',
		progress: 100,
		logs: []
	});

	let intervalId: ReturnType<typeof setInterval> | null = null;

	$effect(() => {
		const d = sakQuery.data;
		if (d) {
			folderId = d.templatesFolderId ?? '';
			zspId = d.zspId ?? '';
		}
	});

	function startSak() {
		runSak(cityId, { templatesFolderId: folderId, zspId })
			.then(() => {
				toast.success('Rozpoczęto generowanie arkuszy');
				sakProgress = { status: 'RUNNING', progress: 0, logs: [] };
			})
			.catch((e) => toast.error(e instanceof Error ? e.message : 'Błąd'));
	}

	function stopSakRun() {
		stopSak(cityId)
			.then(() => {
				toast.success('Zatrzymano generowanie');
				sakProgress = { status: 'STOPPED', progress: 100, logs: [] };
			})
			.catch((e) => toast.error(e instanceof Error ? e.message : 'Błąd'));
	}

	onMount(() => {
		getSakStatus(cityId).then((p) => {
			sakProgress = { ...p, logs: p.logs ?? [] };
		});
		intervalId = setInterval(() => {
			if (cityId && sakProgress.status === 'RUNNING') {
				getSakStatus(cityId).then((p) => {
					sakProgress = { ...p, logs: p.logs ?? [] };
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
			<Card.Title>SAK</Card.Title>
			<Card.Description>Systemowy Arkusz Kontrolny — generowanie arkuszy z szablonów</Card.Description>
		</Card.Header>
		<Card.Content>
			{#if sakQuery.isPending}
				<div class="flex items-center gap-3 py-4">
					<Spinner size="sm" />
					<span class="text-muted-foreground">Ładowanie...</span>
				</div>
			{:else if sakQuery.error}
				<div class="rounded-lg border border-destructive/30 bg-destructive/5 p-4">
					<p class="text-sm text-destructive">{String(sakQuery.error)}</p>
				</div>
			{:else}
				<div class="grid gap-4 max-w-md">
					<div class="space-y-2">
						<Label for="folderId">Folder z arkuszami</Label>
						<Input id="folderId" bind:value={folderId} placeholder="ID folderu" />
					</div>
					<div class="space-y-2">
						<Label for="zspId">ZSP ID</Label>
						<Input id="zspId" bind:value={zspId} placeholder="ZSP ID" />
					</div>
				</div>
			{/if}
		</Card.Content>
	</Card.Root>

	{#if !sakQuery.isPending && !sakQuery.error}
		<ProgressPanel
			status={sakProgress.status}
			progress={sakProgress.progress}
			logs={sakProgress.logs}
			onstart={startSak}
			onstop={stopSakRun}
			startLabel="Generuj arkusze"
			stopLabel="Zatrzymaj generowanie"
		/>
	{/if}
</div>
