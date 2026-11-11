<script lang="ts">
	import * as Table from '$lib/components/ui/table/index.js';
	import { Button } from '$lib/components/ui/button/index.js';
	import { Input } from '$lib/components/ui/input/index.js';
	import * as Select from '$lib/components/ui/select/index.js';
	import {
		createOdysejaQuery,
		createPostMutation,
		createPutMutation,
		createDelMutation
	} from '$lib/queries';
	import IconTrash from '@tabler/icons-svelte/icons/trash';
	import IconPlus from '@tabler/icons-svelte/icons/plus';
	import IconEdit from '@tabler/icons-svelte/icons/edit';
	import IconBulb from '@tabler/icons-svelte/icons/bulb';
	import { toast } from 'svelte-sonner';
	import { setBreadcrumbs } from '$lib/breadcrumbs';
	import { onMount } from 'svelte';
	import { slide } from 'svelte/transition';
	import { getTypeDisplayName, isManual } from '$lib/utils/spontan';

	onMount(() => {
		setBreadcrumbs([
			{ name: 'Konkursy', href: '/dashboard/competitions' },
			{ name: 'Edytor spontanów', href: '/dashboard/spontan-editor' }
		]);
	});

	type SpontanField = {
		id?: number;
		name: string;
		multiplier: number;
		fieldType: 'MULTIPLIER' | 'EXPRESSION' | 'BOOLEAN';
		expression?: string;
		trueValue?: number;
		trueResponse?: string;
		falseResponse?: string;
	};

	type SpontanDefinition = {
		id?: number;
		name: string;
		type: 'VERBAL' | 'MANUAL' | 'VERBAL_MANUAL';
		multiplier?: number;
		fields: SpontanField[];
	};

	type SpontanDefinitionPayload = {
		id?: number;
		name: string;
		type: 'VERBAL' | 'MANUAL' | 'VERBAL_MANUAL';
		multiplier?: number;
		fields: {
			id?: number;
			name: string;
			multiplier: number;
			fieldType: 'MULTIPLIER' | 'EXPRESSION' | 'BOOLEAN';
			expression?: string;
			trueValue?: number;
		}[];
	};

	let editingSpontan = $state<SpontanDefinition | null>(null);
	let isCreating = $state(false);

	let newName = $state('');
	let newType = $state<'VERBAL' | 'MANUAL' | 'VERBAL_MANUAL'>('VERBAL');
	let newMultiplier = $state<number>(1.0);
	let newFields = $state<SpontanField[]>([]);

	let spontansQuery = createOdysejaQuery<SpontanDefinition[]>({
		queryKey: ['spontans'],
		path: '/api/v1/spontan'
	});

	let createMutation = createPostMutation<SpontanDefinition, SpontanDefinition>({
		path: '/api/v1/spontan',
		queryKey: ['spontans'],
		onSuccess: () => {
			resetForm();
			toast.success('Spontan został utworzony');
		}
	});

	let updateMutation = createPutMutation<SpontanDefinition, SpontanDefinition & { id: number }>({
		path: (vars) => `/api/v1/spontan/${vars.id}`,
		queryKey: ['spontans'],
		onSuccess: () => {
			resetForm();
			toast.success('Spontan został zaktualizowany');
		}
	});

	let deleteMutation = createDelMutation<void, { id: number }>({
		path: (vars) => `/api/v1/spontan/${vars.id}`,
		queryKey: ['spontans'],
		onSuccess: () => {
			toast.success('Spontan został usunięty');
		}
	});

	function resetForm() {
		isCreating = false;
		editingSpontan = null;
		newName = '';
		newType = 'VERBAL';
		newMultiplier = 1.0;
		newFields = [];
	}

	function startCreate() {
		resetForm();
		isCreating = true;
	}

	function startEdit(spontan: SpontanDefinition) {
		isCreating = false;
		editingSpontan = spontan;
		newName = spontan.name;
		newType = spontan.type;
		newMultiplier = spontan.multiplier ?? 1.0;
		newFields = spontan.fields.map((f) => ({ ...f, fieldType: f.fieldType ?? 'MULTIPLIER' }));
	}

	function addField() {
		newFields = [...newFields, { name: '', multiplier: 1.0, fieldType: 'MULTIPLIER' }];
	}

	function removeField(index: number) {
		newFields = newFields.filter((_, i) => i !== index);
	}

	function handleSave() {
		const trimmed = newName.trim();
		if (!trimmed) return;

		const fields = isManual(newType)
			? newFields
					.filter((f) => f.name.trim())
					.map((f) => ({
						id: f.id,
						name: f.name,
						multiplier: f.multiplier,
						fieldType: f.fieldType,
						expression: f.fieldType === 'EXPRESSION' ? f.expression : undefined,
						trueValue: f.fieldType === 'BOOLEAN' ? (f.trueValue ?? 0) : undefined,
						trueResponse:
							f.fieldType === 'BOOLEAN' ? f.trueResponse?.trim() || undefined : undefined,
						falseResponse:
							f.fieldType === 'BOOLEAN' ? f.falseResponse?.trim() || undefined : undefined
					}))
			: [];

		const definition: SpontanDefinitionPayload = {
			name: trimmed,
			type: newType,
			multiplier: isManual(newType) ? newMultiplier : undefined,
			fields
		};

		if (editingSpontan?.id) {
			updateMutation.mutate({ ...definition, id: editingSpontan.id } as any);
		} else {
			createMutation.mutate(definition as any);
		}
	}

	function handleDelete(spontan: SpontanDefinition) {
		if (spontan.id) {
			deleteMutation.mutate({ id: spontan.id });
		}
	}

	let hasExpressionField = $derived(newFields.some((f) => f.fieldType === 'EXPRESSION'));
	let isEditing = $derived(isCreating || !!editingSpontan);
</script>

{#snippet editForm()}
	<div class="flex flex-col gap-4">
		<div>
			<label class="mb-1 block text-sm font-medium">Nazwa</label>
			<Input bind:value={newName} placeholder="Nazwa spontana..." />
		</div>

		<div>
			<label class="mb-1 block text-sm font-medium">Typ</label>
			<Select.Root
				type="single"
				value={newType}
				onValueChange={(v) => {
					if (v === 'VERBAL' || v === 'MANUAL' || v === 'VERBAL_MANUAL') newType = v;
				}}
			>
				<Select.Trigger class="w-full">
					{getTypeDisplayName(newType)}
				</Select.Trigger>
				<Select.Content>
					<Select.Item value="VERBAL">Słowny</Select.Item>
					<Select.Item value="MANUAL">Manualny</Select.Item>
					<Select.Item value="VERBAL_MANUAL">Manualno Słowny</Select.Item>
				</Select.Content>
			</Select.Root>
		</div>

		{#if isManual(newType)}
			<div>
				<div class="mb-2 flex items-center justify-between">
					<label class="text-sm font-medium">Pola</label>
				</div>

				{#if newFields.length > 0}
					<div class="flex flex-col gap-2">
						{#each newFields as field, i}
							<div class="flex flex-wrap items-center gap-2">
								<Input
									bind:value={field.name}
									placeholder="Nazwa pola..."
									class="w-full sm:w-200"
								/>
								<Select.Root
									type="single"
									value={field.fieldType}
									onValueChange={(v) => {
										if (v === 'MULTIPLIER' || v === 'EXPRESSION' || v === 'BOOLEAN')
											newFields[i].fieldType = v;
									}}
								>
									<Select.Trigger class="w-full sm:w-32">
										{field.fieldType === 'MULTIPLIER'
											? 'Mnożnik'
											: field.fieldType === 'EXPRESSION'
												? 'Wyrażenie'
												: 'TAK/NIE'}
									</Select.Trigger>
									<Select.Content>
										<Select.Item value="MULTIPLIER">Mnożnik</Select.Item>
										<Select.Item value="EXPRESSION">Wyrażenie</Select.Item>
										<Select.Item value="BOOLEAN">TAK/NIE</Select.Item>
									</Select.Content>
								</Select.Root>
								{#if field.fieldType === 'EXPRESSION'}
									<Input
										bind:value={field.expression}
										placeholder="np. FLOOR(v/3)*5"
										class="w-full font-mono sm:w-48"
									/>
								{:else if field.fieldType === 'BOOLEAN'}
									<Input
										type="number"
										step="0.1"
										bind:value={field.trueValue}
										placeholder="Pkt. za TAK"
										class="w-full sm:w-30"
									/>
									<Input
										type="text"
										bind:value={field.trueResponse}
										placeholder="Etykieta TAK (Tak)"
										aria-label="Etykieta dla TAK"
										class="w-full sm:w-40"
									/>
									<Input
										type="text"
										bind:value={field.falseResponse}
										placeholder="Etykieta NIE (Nie)"
										aria-label="Etykieta dla NIE"
										class="w-full sm:w-40"
									/>
								{:else}
									<Input
										type="number"
										step="0.1"
										bind:value={field.multiplier}
										placeholder="Mnożnik"
										class="w-full sm:w-24"
									/>
								{/if}
								<Button variant="ghost" size="icon" onclick={() => removeField(i)}>
									<IconTrash class="size-4 text-red-500" />
								</Button>
							</div>
						{/each}
					</div>
				{:else}
					<p class="text-muted-foreground text-sm">Brak pól. Dodaj pola powyżej.</p>
				{/if}
				<Button variant="outline" size="sm" onclick={addField} class="mt-2">
					<IconPlus class="mr-1 size-3" />
					Dodaj pole
				</Button>

				{#if hasExpressionField}
					<div class="bg-muted/30 mt-2 rounded-md border p-3 text-sm">
						<p class="mb-1 font-medium">Składnia wyrażeń</p>
						<ul class="text-muted-foreground list-inside list-disc space-y-0.5">
							<li>
								<code class="bg-muted rounded px-1 font-mono text-xs">v</code> — wartość wpisana w pole
							</li>
							<li>
								Funkcje: <code class="bg-muted rounded px-1 font-mono text-xs">FLOOR</code>,
								<code class="bg-muted rounded px-1 font-mono text-xs">CEILING</code>,
								<code class="bg-muted rounded px-1 font-mono text-xs">ROUND</code>,
								<code class="bg-muted rounded px-1 font-mono text-xs">MIN</code>,
								<code class="bg-muted rounded px-1 font-mono text-xs">MAX</code>,
								<code class="bg-muted rounded px-1 font-mono text-xs">ABS</code>,
								<code class="bg-muted rounded px-1 font-mono text-xs">SQRT</code>
							</li>
							<li>
								Przykłady: <code class="bg-muted rounded px-1 font-mono text-xs">FLOOR(v/3)*5</code
								>, <code class="bg-muted rounded px-1 font-mono text-xs">MIN(v, 100)</code>,
								<code class="bg-muted rounded px-1 font-mono text-xs">v*1.5</code>
							</li>
						</ul>
					</div>
				{/if}
			</div>
		{/if}

		<div class="flex gap-2 pt-2">
			<Button
				onclick={handleSave}
				disabled={!newName.trim() || createMutation.isPending || updateMutation.isPending}
			>
				{editingSpontan ? 'Zapisz' : 'Utwórz'}
			</Button>
			<Button variant="outline" onclick={resetForm}>Anuluj</Button>
		</div>
	</div>
{/snippet}

<div class="flex flex-col gap-6">
	<div class="flex items-center justify-between">
		<div class="flex items-center gap-3">
			<div class="bg-primary/10 flex size-10 items-center justify-center rounded-lg">
				<IconBulb class="text-primary size-5" />
			</div>
			<div>
				<h1 class="text-2xl font-semibold tracking-tight">Edytor spontanów</h1>
				<p class="text-muted-foreground text-sm">
					{#if spontansQuery.data && spontansQuery.data.length > 0}
						{spontansQuery.data.length} {spontansQuery.data.length === 1 ? 'spontan' : 'spontanów'}
					{:else}
						Zarządzaj definicjami spontanów
					{/if}
				</p>
			</div>
		</div>
		<Button onclick={startCreate}>
			<IconPlus class="mr-1 size-4" />
			Nowy spontan
		</Button>
	</div>

	{#if spontansQuery.isPending}
		<div class="text-muted-foreground text-sm">Ładowanie...</div>
	{:else if spontansQuery.error}
		<div class="border-destructive/30 bg-destructive/5 rounded-lg border p-6 text-center">
			<p class="text-destructive font-medium">Błąd podczas ładowania spontanów</p>
		</div>
	{:else if spontansQuery.data && (spontansQuery.data.length > 0 || isCreating)}
		<div class="bg-card w-full overflow-hidden rounded-xl border shadow-sm">
			<Table.Root>
				<Table.Header>
					<Table.Row class="bg-muted/40 hover:bg-muted/40">
						<Table.Head class="font-semibold">Nazwa</Table.Head>
						<Table.Head class="font-semibold">Typ</Table.Head>
						<Table.Head class="font-semibold">Pola</Table.Head>
						<Table.Head class="w-24"></Table.Head>
					</Table.Row>
				</Table.Header>
				<Table.Body>
					{#each spontansQuery.data as spontan (spontan.id)}
						<Table.Row
							class={editingSpontan?.id === spontan.id ? 'bg-muted/50 hover:bg-muted/50' : ''}
						>
							<Table.Cell class="font-medium">{spontan.name}</Table.Cell>
							<Table.Cell>
								{getTypeDisplayName(spontan.type)}
							</Table.Cell>
							<Table.Cell>
								{#if isManual(spontan.type)}
									{spontan.fields.length} {spontan.fields.length === 1 ? 'pole' : 'pól'}
								{:else}
									—
								{/if}
							</Table.Cell>
							<Table.Cell class="text-right">
								<div class="flex justify-end gap-1">
									<Button
										variant="ghost"
										size="icon"
										onclick={() => startEdit(spontan)}
										disabled={isEditing}
									>
										<IconEdit class="size-4" />
									</Button>
									<Button
										variant="ghost"
										size="icon"
										onclick={() => handleDelete(spontan)}
										disabled={deleteMutation.isPending || isEditing}
									>
										<IconTrash class="size-4 text-red-500" />
									</Button>
								</div>
							</Table.Cell>
						</Table.Row>
						{#if editingSpontan?.id === spontan.id}
							<tr>
								<td colspan="4" class="p-0">
									<div transition:slide={{ duration: 200 }} class="bg-muted/30 px-5 py-4">
										{@render editForm()}
									</div>
								</td>
							</tr>
						{/if}
					{/each}
					{#if isCreating}
						<tr>
							<td colspan="4" class="p-0">
								<div transition:slide={{ duration: 200 }} class="bg-muted/30 px-5 py-4">
									{@render editForm()}
								</div>
							</td>
						</tr>
					{/if}
				</Table.Body>
			</Table.Root>
		</div>
	{:else if !isCreating}
		<div class="max-w-2xl rounded-lg border border-dashed p-12 text-center">
			<IconBulb class="text-muted-foreground/40 mx-auto mb-3 size-10" />
			<p class="text-muted-foreground font-medium">Brak spontanów</p>
			<p class="text-muted-foreground/70 mt-1 text-sm">
				Utwórz pierwszy spontan klikając "Nowy spontan".
			</p>
		</div>
	{/if}
</div>
