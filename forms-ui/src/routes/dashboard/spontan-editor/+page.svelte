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

    onMount(() => {
        setBreadcrumbs([
            {name: 'Dashboard', href: '/dashboard'},
            {name: 'Edytor spontanów', href: '/dashboard/spontan-editor'},
        ]);
    });

    type SpontanField = {
        id?: number;
        name: string;
        multiplier: number;
    };

    type SpontanDefinition = {
        id?: number;
        name: string;
        type: 'VERBAL' | 'MANUAL';
        multiplier?: number;
        fields: SpontanField[];
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
        newFields = spontan.fields.map(f => ({...f}));
    }

    function addField() {
        newFields = [...newFields, {name: '', multiplier: 1.0}];
    }

    function removeField(index: number) {
        newFields = newFields.filter((_, i) => i !== index);
    }

    function handleSave() {
        const trimmed = newName.trim();
        if (!trimmed) return;

        const definition: SpontanDefinition = {
            name: trimmed,
            type: newType,
            multiplier: newType === 'MANUAL' ? newMultiplier : undefined,
            fields: newType === 'MANUAL' ? newFields.filter(f => f.name.trim()) : [],
        };

        if (editingSpontan?.id) {
            updateMutation.mutate({...definition, id: editingSpontan.id});
        } else {
            createMutation.mutate(definition);
        }
    }

    function handleDelete(spontan: SpontanDefinition) {
        if (spontan.id) {
            deleteMutation.mutate({id: spontan.id});
        }
    }
</script>

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
        {#if !isCreating && !editingSpontan}
            <Button onclick={startCreate}>
                <IconPlus class="size-4 mr-1"/>
                Nowy spontan
            </Button>
        {/if}
    </div>

    {#if isCreating || editingSpontan}
        <div class="rounded-xl border bg-card shadow-sm p-6 max-w-2xl">
            <h2 class="text-lg font-semibold mb-4">
                {editingSpontan ? 'Edytuj spontan' : 'Nowy spontan'}
            </h2>
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
                            onValueChange={(v) => { if (v) newType = v as 'VERBAL' | 'MANUAL'; }}
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
                        <label class="text-sm font-medium mb-1 block">Mnożnik</label>
                        <Input
                                type="number"
                                step="0.1"
                                bind:value={newMultiplier}
                                placeholder="Mnożnik..."
                        />
                    </div>

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
                                        <Input
                                                type="number"
                                                step="0.1"
                                                bind:value={field.multiplier}
                                                placeholder="Mnożnik"
                                                class="w-24"
                                        />
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
        </div>
    {/if}

    {#if spontansQuery.isPending}
        <div class="text-muted-foreground text-sm">Ładowanie...</div>
    {:else if spontansQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd podczas ładowania spontanów</p>
        </div>
    {:else if spontansQuery.data && spontansQuery.data.length > 0}
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
                        <Table.Row>
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
                                    >
                                        <IconEdit class="size-4"/>
                                    </Button>
                                    <Button
                                            variant="ghost"
                                            size="icon"
                                            onclick={() => handleDelete(spontan)}
                                            disabled={deleteMutation.isPending}
                                    >
                                        <IconTrash class="size-4 text-red-500"/>
                                    </Button>
                                </div>
                            </Table.Cell>
                        </Table.Row>
                    {/each}
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
