<script lang="ts">
	import { Spinner } from '$lib/components/ui/spinner';
	import * as Table from '$lib/components/ui/table';
	import { setBreadcrumbs } from '$lib/breadcrumbs';
	import { onMount } from 'svelte';
	import { createOdysejaQuery, createPutMutation } from '$lib/queries';
	import { Button } from '$lib/components/ui/button';
	import { toast } from 'svelte-sonner';
	import IconForms from '@tabler/icons-svelte/icons/forms';
	import TileStyleButton from '$lib/components/TileStyleButton.svelte';

	type Age = {
		id: number;
		icon: string | null;
		color: string | null;
	};

	const ageLabels = ['Juniorzy', 'Grupa I', 'Grupa II', 'Grupa III', 'Grupa IV'];

	let agesQuery = createOdysejaQuery<Age[]>({
		queryKey: ['ages'],
		path: '/api/age'
	});

	let draftAges = $state<Age[]>([]);
	let agesSnapshot = $state('');

	$effect(() => {
		const ages = agesQuery.data;
		if (ages?.length && !draftAges.length) {
			draftAges = ages.map((a) => ({ id: a.id, icon: a.icon ?? null, color: a.color ?? null }));
			agesSnapshot = JSON.stringify(draftAges);
		}
	});

	let agesDirty = $derived(JSON.stringify(draftAges) !== agesSnapshot);

	let saveAgesMutation = createPutMutation<void, Age[]>({
		path: '/api/age',
		queryKey: ['ages'],
		onSuccess: () => {
			toast.success('Grupy wiekowe zaktualizowane');
			agesSnapshot = JSON.stringify(draftAges);
		}
	});

	onMount(() => {
		setBreadcrumbs([{ name: 'Grupy wiekowe', href: '/ages' }]);
	});
</script>

<div class="flex flex-col gap-6">
	<div class="flex flex-col gap-1">
		<div class="flex w-full items-stretch gap-3">
			<div class="bg-primary/10 flex size-10 items-center justify-center rounded-lg">
				<IconForms class="text-primary size-5" />
			</div>
			<div class="flex-1">
				<h1 class="text-2xl font-semibold tracking-tight">Grupy wiekowe</h1>
			</div>
		</div>
	</div>

	<div class="flex flex-col gap-4">
		<div class="flex items-center justify-between gap-3">
			<div>
				<h2 class="text-lg font-semibold tracking-tight">Grupy wiekowe</h2>
				<p class="text-muted-foreground text-sm">
					Wygląd kafelków grup wiekowych w harmonogramie aplikacji.
				</p>
			</div>
		</div>

		{#if agesQuery.error}
			<div class="text-red-500">{String(agesQuery.error)}</div>
		{:else if agesQuery.isPending}
			<Spinner size="sm" />
		{:else}
			<div class="overflow-x-auto rounded-md border">
				<Table.Root>
					<Table.Header>
						<Table.Row>
							<Table.Head class="w-24">Id</Table.Head>
							<Table.Head>Grupa</Table.Head>
							<Table.Head class="w-44">Kafelek w harmonogramie</Table.Head>
						</Table.Row>
					</Table.Header>
					<Table.Body>
						{#each draftAges as age (age.id)}
							<Table.Row>
								<Table.Cell class="align-middle font-mono text-sm">{age.id}</Table.Cell>
								<Table.Cell class="align-middle"
									>{ageLabels[age.id] ?? `Grupa ${age.id}`}</Table.Cell
								>
								<Table.Cell class="align-middle">
									<TileStyleButton
										bind:icon={age.icon}
										bind:color={age.color}
										label={ageLabels[age.id] ?? `Grupa ${age.id}`}
										defaultIcon="users-01"
										defaultColorIndex={age.id}
										onSave={() => saveAgesMutation.mutate(draftAges)}
									/>
								</Table.Cell>
							</Table.Row>
						{/each}
					</Table.Body>
				</Table.Root>
			</div>
		{/if}
	</div>
</div>
