<script lang="ts">
	import { page } from '$app/state';
	import {
		downloadRakPdf,
		downloadRakShortPdf,
		downloadRakLatexPdf
	} from '$lib/zwierzyniec';
	import { createOdysejaQuery } from '$lib/queries';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Spinner } from '$lib/components/ui/spinner';
	import { toast } from 'svelte-sonner';

	let cityId = $derived(Number(page.params.cityId));

	let rakQuery = $derived(
		createOdysejaQuery<{ zspId: string; contestName?: string }>({
			queryKey: ['rak', String(cityId)],
			path: `/api/v1/rak?cityId=${cityId}`
		})
	);

	let zspId = $state('');
	let contestName = $state('');
	let isRegion = $state(false);
	let isLoading = $state(false);

	$effect(() => {
		const d = rakQuery.data;
		if (d) {
			zspId = d.zspId ?? '';
			contestName = d.contestName ?? '';
		}
	});

	async function downloadFile(
		fn: (cityId: number, zspId: string, isRegion: boolean, contestName?: string) => Promise<ArrayBuffer>,
		extension: string
	) {
		try {
			isLoading = true;
			const content = await fn(cityId, zspId, isRegion, contestName || undefined);
			const blob = new Blob([content], { type: 'application/pdf' });
			const url = URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = `tm_${zspId}.${extension}`;
			a.style.display = 'none';
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
			URL.revokeObjectURL(url);
			toast.success('PDF wygenerowany');
		} catch (e) {
			toast.error(e instanceof Error ? e.message : 'Błąd generowania');
		} finally {
			isLoading = false;
		}
	}

	const downloadPdf = () => downloadFile(downloadRakPdf, 'pdf');
	const downloadShortPdf = () => downloadFile(downloadRakShortPdf, 'pdf');
	const downloadLatexPdf = () => downloadFile(downloadRakLatexPdf, 'pdf');
</script>

<div class="flex flex-col gap-5">
	<h2 class="text-xl font-semibold">Rankingowy Analizator Końcowy</h2>

	{#if rakQuery.isPending}
		<div class="flex items-center gap-3 py-4">
			<Spinner size="sm" />
			<span class="text-muted-foreground">Ładowanie...</span>
		</div>
	{:else if rakQuery.error}
		<div class="rounded-lg border border-destructive/30 bg-destructive/5 p-4">
			<p class="text-sm text-destructive">{String(rakQuery.error)}</p>
		</div>
	{:else}
		<div class="flex flex-col gap-3 max-w-md">
			<div class="space-y-2">
				<Label for="zspId">ZSP ID</Label>
				<Input id="zspId" type="text" bind:value={zspId} placeholder="ZSP ID" />
			</div>
			<div class="space-y-2">
				<Label for="contestName">Nazwa konkursu</Label>
				<Input id="contestName" type="text" bind:value={contestName} placeholder="Nazwa konkursu" />
			</div>
			<div class="flex items-center gap-2">
				<input type="checkbox" id="isRegion" bind:checked={isRegion} class="rounded" />
				<Label for="isRegion">Region (FR)</Label>
			</div>
			<div class="flex flex-wrap gap-3 items-center">
				<Button onclick={downloadPdf} disabled={isLoading}>Generuj PDF results</Button>
				<Button variant="secondary" onclick={downloadShortPdf} disabled={isLoading}>
					Generuj skrócony PDF
				</Button>
				<Button variant="secondary" onclick={downloadLatexPdf} disabled={isLoading}>
					Generuj PDF (LaTeX)
				</Button>
				{#if isLoading}
					<Spinner size="sm" />
				{/if}
			</div>
		</div>
	{/if}
</div>
