<script lang="ts">
	import { page } from '$app/state';
	import { goto } from '$app/navigation';
	import { createOdysejaQuery } from '$lib/queries';
	import { setBreadcrumbs } from '$lib/breadcrumbs';
	import { onMount } from 'svelte';
	import IconFileText from '@tabler/icons-svelte/icons/file-text';
	import IconLayoutList from '@tabler/icons-svelte/icons/layout-list';
	import IconFileSpreadsheet from '@tabler/icons-svelte/icons/file-spreadsheet';
	import IconTool from '@tabler/icons-svelte/icons/tool';

	type City = {
		id: number;
		name: string;
	};

	let cityId = $derived(Number(page.params.cityId));

	let citiesQuery = $derived(
		createOdysejaQuery<City[]>({
			queryKey: ['dashboardCities'],
			path: '/api/v1/dashboard/cities'
		})
	);

	let cityName = $derived.by(() => {
		if (!citiesQuery.data) return '...';
		return citiesQuery.data.find((c: City) => c.id === cityId)?.name ?? '...';
	});

	const tabs = [
		{ id: 'rak', label: 'RAK', icon: IconFileText },
		{ id: 'gad', label: 'GAD', icon: IconLayoutList },
		{ id: 'sak', label: 'SAK', icon: IconFileSpreadsheet },
		{ id: 'fixer', label: 'Fixer', icon: IconTool }
	];

	let activeTab = $derived.by(() => {
		const path = page.url.pathname;
		const found = tabs.find((t) => path.includes(`/${t.id}`));
		return found?.id ?? 'rak';
	});

	$effect(() => {
		setBreadcrumbs([
			{ name: 'Zwierzyniec', href: '/dashboard/zwierzyniec' },
			{ name: cityName, href: `/dashboard/zwierzyniec/${cityId}/rak` }
		]);
	});
</script>

<div class="flex flex-col gap-4 h-full">
	<div class="flex items-center justify-between">
		<div class="flex gap-1 rounded-lg bg-muted p-1 w-fit">
			{#each tabs as tab (tab.id)}
				<button
					class="flex items-center gap-1.5 rounded-md px-3 py-1.5 text-sm font-medium transition-colors {activeTab === tab.id
						? 'bg-background shadow-sm'
						: 'text-muted-foreground hover:text-foreground'}"
					onclick={() => goto(`/dashboard/zwierzyniec/${cityId}/${tab.id}`)}
				>
					<tab.icon class="size-4" />
					{tab.label}
				</button>
			{/each}
		</div>
	</div>

	<slot />
</div>
