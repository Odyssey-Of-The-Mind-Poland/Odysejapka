<script lang="ts">
    import * as Table from '$lib/components/ui/table/index.js';
    import {Button} from '$lib/components/ui/button/index.js';
    import {Input} from '$lib/components/ui/input/index.js';
    import * as Select from '$lib/components/ui/select/index.js';
    import {createOdysejaQuery, createPostMutation, createPutMutation, createDelMutation} from '$lib/queries';
    import IconTrash from '@tabler/icons-svelte/icons/trash';
    import IconPlus from '@tabler/icons-svelte/icons/plus';
    import IconEdit from '@tabler/icons-svelte/icons/edit';
    import IconBulb from '@tabler/icons-svelte/icons/bulb';
    import {toast} from 'svelte-sonner';
    import {setBreadcrumbs} from '$lib/breadcrumbs';
    import {onMount} from 'svelte';
    import {slide} from 'svelte/transition';

    onMount(() => {
        setBreadcrumbs([
            {name: 'Konkursy', href: '/dashboard/competitions'},
            {name: 'Edytor spontanów', href: '/dashboard/spontan-editor'},
        ]);
    });

    type SpontanField = {
        id?: number;
        name: string;
        multiplier: number;
        fieldType: 'MULTIPLIER' | 'EXPRESSION';
        expression?: string;
    };

    type SpontanDefinition = {
        id?: number;
        name: string;
        type: 'VERBAL' | 'MANUAL';
        multiplier?: number;
        fields: SpontanField[];
    };

    type SpontanDefinitionPayload = {
        id?: number;
        name: string;
        type: 'VERBAL' | 'MANUAL';
        multiplier?: number;
        fields: {
            id?: number;
            name: string;
            multiplier: number;
            fieldType: 'MULTIPLIER' | 'EXPRESSION';
            expression?: string;
        }[];
    };

    let editingSpontan = $state<SpontanDefinition | null>(null);
    let isCreating = $state(false);

    let newName = $state('');
    let newType = $state<'VERBAL' | 'MANUAL'>('VERBAL');
    let newMultiplier = $state<number>(1.0);
    let newFields = $state<SpontanField[]>([]);

    let spontansQuery = createOdysejaQuery<SpontanDefinition[]>({
        queryKey: ['spontans'],
        path: '/api/v1/spontan',
    });

    let createMutation = createPostMutation<SpontanDefinition, SpontanDefinition>({
        path: '/api/v1/spontan',
        queryKey: ['spontans'],
        onSuccess: () => {
            resetForm();
            toast.success('Spontan został utworzony');
        },
    });

    let updateMutation = createPutMutation<SpontanDefinition, SpontanDefinition & { id: number }>({
        path: (vars) => `/api/v1/spontan/${vars.id}`,
        queryKey: ['spontans'],
        onSuccess: () => {
            resetForm();
            toast.success('Spontan został zaktualizowany');
        },
    });

    let deleteMutation = createDelMutation<void, { id: number }>({
        path: (vars) => `/api/v1/spontan/${vars.id}`,
        queryKey: ['spontans'],
        onSuccess: () => {
            toast.success('Spontan został usunięty');
        },
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
        newFields = spontan.fields.map(f => ({...f, fieldType: f.fieldType ?? 'MULTIPLIER'}));
    }

    function addField() {
        newFields = [...newFields, {name: '', multiplier: 1.0, fieldType: 'MULTIPLIER'}];
    }

    function removeField(index: number) {
        newFields = newFields.filter((_, i) => i !== index);
    }

    function handleSave() {
        const trimmed = newName.trim();
        if (!trimmed) return;

        const fields = newType === 'MANUAL'
            ? newFields.filter(f => f.name.trim()).map(f => ({
                id: f.id,
                name: f.name,
                multiplier: f.multiplier,
                fieldType: f.fieldType,
                expression: f.fieldType === 'EXPRESSION' ? f.expression : undefined,
            }))
            : [];

        const definition: SpontanDefinitionPayload = {
            name: trimmed,
            type: newType,
            multiplier: newType === 'MANUAL' ? newMultiplier : undefined,
            fields,
        };

        if (editingSpontan?.id) {
            updateMutation.mutate({...definition, id: editingSpontan.id} as any);
        } else {
            createMutation.mutate(definition as any);
        }
    }

    function handleDelete(spontan: SpontanDefinition) {
        if (spontan.id) {
            deleteMutation.mutate({id: spontan.id});
        }
    }

    let hasExpressionField = $derived(newFields.some(f => f.fieldType === 'EXPRESSION'));
    let isEditing = $derived(isCreating || !!editingSpontan);
</script>

{#snippet editForm()}
    <div class="flex flex-col gap-4">
        <div>
            <label class="text-sm font-medium mb-1 block">Nazwa</label>
            <Input bind:value={newName} placeholder="Nazwa spontana..."/>
        </div>

        <div>
            <label class="text-sm font-medium mb-1 block">Typ</label>
            <Select.Root
                    type="single"
                    value={newType}
                    onValueChange={(v) => { if (v === 'VERBAL' || v === 'MANUAL') newType = v; }}
            >
                <Select.Trigger class="w-full">
                    {newType === 'VERBAL' ? 'Słowny' : 'Manualny'}
                </Select.Trigger>
                <Select.Content>
                    <Select.Item value="VERBAL">Słowny</Select.Item>
                    <Select.Item value="MANUAL">Manualny</Select.Item>
                </Select.Content>
            </Select.Root>
        </div>

        {#if newType === 'MANUAL'}
            <div>
                <div class="flex items-center justify-between mb-2">
                    <label class="text-sm font-medium">Pola</label>
                    <Button variant="outline" size="sm" onclick={addField}>
                        <IconPlus class="size-3 mr-1"/>
                        Dodaj pole
                    </Button>
                </div>

                {#if newFields.length > 0}
                    <div class="flex flex-col gap-2">
                        {#each newFields as field, i}
                            <div class="flex items-center gap-2">
                                <Input
                                        bind:value={field.name}
                                        placeholder="Nazwa pola..."
                                        class="flex-1"
                                />
                                <Select.Root
                                        type="single"
                                        value={field.fieldType}
                                        onValueChange={(v) => { if (v === 'MULTIPLIER' || v === 'EXPRESSION') newFields[i].fieldType = v; }}
                                >
                                    <Select.Trigger class="w-32">
                                        {field.fieldType === 'MULTIPLIER' ? 'Mnożnik' : 'Wyrażenie'}
                                    </Select.Trigger>
                                    <Select.Content>
                                        <Select.Item value="MULTIPLIER">Mnożnik</Select.Item>
                                        <Select.Item value="EXPRESSION">Wyrażenie</Select.Item>
                                    </Select.Content>
                                </Select.Root>
                                {#if field.fieldType === 'EXPRESSION'}
                                    <Input
                                            bind:value={field.expression}
                                            placeholder="np. FLOOR(v/3)*5"
                                            class="w-48 font-mono"
                                    />
                                {:else}
                                    <Input
                                            type="number"
                                            step="0.1"
                                            bind:value={field.multiplier}
                                            placeholder="Mnożnik"
                                            class="w-24"
                                    />
                                {/if}
                                <Button
                                        variant="ghost"
                                        size="icon"
                                        onclick={() => removeField(i)}
                                >
                                    <IconTrash class="size-4 text-red-500"/>
                                </Button>
                            </div>
                        {/each}
                    </div>
                {:else}
                    <p class="text-sm text-muted-foreground">Brak pól. Dodaj pola powyżej.</p>
                {/if}

                {#if hasExpressionField}
                    <div class="rounded-md border bg-muted/30 p-3 mt-2 text-sm">
                        <p class="font-medium mb-1">Składnia wyrażeń</p>
                        <ul class="list-disc list-inside text-muted-foreground space-y-0.5">
                            <li><code class="font-mono text-xs bg-muted px-1 rounded">v</code> — wartość wpisana w pole</li>
                            <li>Funkcje: <code class="font-mono text-xs bg-muted px-1 rounded">FLOOR</code>, <code class="font-mono text-xs bg-muted px-1 rounded">CEILING</code>, <code class="font-mono text-xs bg-muted px-1 rounded">ROUND</code>, <code class="font-mono text-xs bg-muted px-1 rounded">MIN</code>, <code class="font-mono text-xs bg-muted px-1 rounded">MAX</code>, <code class="font-mono text-xs bg-muted px-1 rounded">ABS</code>, <code class="font-mono text-xs bg-muted px-1 rounded">SQRT</code></li>
                            <li>Przykłady: <code class="font-mono text-xs bg-muted px-1 rounded">FLOOR(v/3)*5</code>, <code class="font-mono text-xs bg-muted px-1 rounded">MIN(v, 100)</code>, <code class="font-mono text-xs bg-muted px-1 rounded">v*1.5</code></li>
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
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconBulb class="size-5 text-primary"/>
            </div>
            <div>
                <h1 class="text-2xl font-semibold tracking-tight">Edytor spontanów</h1>
                <p class="text-sm text-muted-foreground">
                    {#if spontansQuery.data && spontansQuery.data.length > 0}
                        {spontansQuery.data.length} {spontansQuery.data.length === 1 ? 'spontan' : 'spontanów'}
                    {:else}
                        Zarządzaj definicjami spontanów
                    {/if}
                </p>
            </div>
        </div>
        <Button onclick={startCreate}>
            <IconPlus class="size-4 mr-1"/>
            Nowy spontan
        </Button>
    </div>

    {#if spontansQuery.isPending}
        <div class="text-muted-foreground text-sm">Ładowanie...</div>
    {:else if spontansQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd podczas ładowania spontanów</p>
        </div>
    {:else if spontansQuery.data && (spontansQuery.data.length > 0 || isCreating)}
        <div class="rounded-xl border bg-card shadow-sm overflow-hidden max-w-2xl">
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
                        <Table.Row class={editingSpontan?.id === spontan.id ? 'bg-muted/50 hover:bg-muted/50' : ''}>
                            <Table.Cell class="font-medium">{spontan.name}</Table.Cell>
                            <Table.Cell>
                                {spontan.type === 'VERBAL' ? 'Słowny' : 'Manualny'}
                            </Table.Cell>
                            <Table.Cell>
                                {#if spontan.type === 'MANUAL'}
                                    {spontan.fields.length} {spontan.fields.length === 1 ? 'pole' : 'pól'}
                                {:else}
                                    —
                                {/if}
                            </Table.Cell>
                            <Table.Cell class="text-right">
                                <div class="flex gap-1 justify-end">
                                    <Button
                                            variant="ghost"
                                            size="icon"
                                            onclick={() => startEdit(spontan)}
                                            disabled={isEditing}
                                    >
                                        <IconEdit class="size-4"/>
                                    </Button>
                                    <Button
                                            variant="ghost"
                                            size="icon"
                                            onclick={() => handleDelete(spontan)}
                                            disabled={deleteMutation.isPending || isEditing}
                                    >
                                        <IconTrash class="size-4 text-red-500"/>
                                    </Button>
                                </div>
                            </Table.Cell>
                        </Table.Row>
                        {#if editingSpontan?.id === spontan.id}
                            <tr>
                                <td colspan="4" class="p-0">
                                    <div transition:slide={{ duration: 200 }} class="px-5 py-4 bg-muted/30">
                                        {@render editForm()}
                                    </div>
                                </td>
                            </tr>
                        {/if}
                    {/each}
                    {#if isCreating}
                        <tr>
                            <td colspan="4" class="p-0">
                                <div transition:slide={{ duration: 200 }} class="px-5 py-4 bg-muted/30">
                                    {@render editForm()}
                                </div>
                            </td>
                        </tr>
                    {/if}
                </Table.Body>
            </Table.Root>
        </div>
    {:else if !isCreating}
        <div class="rounded-lg border border-dashed p-12 text-center max-w-2xl">
            <IconBulb class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
            <p class="text-muted-foreground font-medium">Brak spontanów</p>
            <p class="text-sm text-muted-foreground/70 mt-1">Utwórz pierwszy spontan klikając "Nowy spontan".</p>
        </div>
    {/if}
</div>
