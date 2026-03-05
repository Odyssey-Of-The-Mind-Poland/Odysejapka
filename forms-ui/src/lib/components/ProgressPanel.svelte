<script lang="ts">
	import { Badge } from '$lib/components/ui/badge';
	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { tick } from 'svelte';

	let {
		status,
		progress,
		logs,
		onstart,
		onstop,
		startLabel = 'Uruchom',
		stopLabel = 'Zatrzymaj',
		disabled = false
	}: {
		status: string;
		progress: number;
		logs: { logTime: string; message: string; level?: string }[];
		onstart: () => void;
		onstop: () => void;
		startLabel?: string;
		stopLabel?: string;
		disabled?: boolean;
	} = $props();

	let logContainer: HTMLDivElement | undefined = $state();

	$effect(() => {
		if (logs.length && logContainer) {
			tick().then(() => {
				if (logContainer) {
					logContainer.scrollTop = logContainer.scrollHeight;
				}
			});
		}
	});

	const statusVariant = $derived.by(() => {
		switch (status) {
			case 'RUNNING':
				return 'default' as const;
			case 'FAILED':
				return 'destructive' as const;
			case 'CANCELLED':
				return 'outline' as const;
			default:
				return 'secondary' as const;
		}
	});

	let showProgress = $derived(status === 'RUNNING' || status === 'FAILED' || logs.length > 0);
</script>

<Card.Root>
	<Card.Header>
		<div class="flex items-center justify-between">
			<Card.Title>Postęp</Card.Title>
			<Badge variant={statusVariant}>{status}</Badge>
		</div>
	</Card.Header>
	<Card.Content class="flex flex-col gap-4">
		{#if status === 'RUNNING'}
			<Button variant="destructive" onclick={onstop}>{stopLabel}</Button>
		{:else}
			<Button onclick={onstart} {disabled}>{startLabel}</Button>
		{/if}

		{#if showProgress}
			<div class="h-2 rounded-full bg-muted overflow-hidden">
				<div
					class="h-full bg-primary transition-all"
					style="width: {progress}%"
				></div>
			</div>
		{/if}

		{#if logs.length > 0}
			<div
				bind:this={logContainer}
				class="flex flex-col gap-0.5 p-3 bg-muted rounded-lg font-mono text-xs max-h-48 overflow-y-auto"
			>
				{#each logs as log}
					<div class="flex gap-2 {log.level === 'ERROR' ? 'text-destructive' : ''}">
						<span class="text-muted-foreground shrink-0">{log.logTime}</span>
						<span>{log.level === 'ERROR' ? 'ERROR: ' : '> '}{log.message}</span>
					</div>
				{/each}
			</div>
		{/if}
	</Card.Content>
</Card.Root>
