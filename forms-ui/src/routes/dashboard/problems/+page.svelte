<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import * as Table from '$lib/components/ui/table';
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {onMount} from "svelte";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {Button} from "$lib/components/ui/button";
    import {Input} from "$lib/components/ui/input";
    import {toast} from "svelte-sonner";
    import IconForms from "@tabler/icons-svelte/icons/forms";
    import TileStyleButton from "$lib/components/TileStyleButton.svelte";

    type Problem = {
        id: number;
        name: string;
        icon: string | null;
        color: string | null;
    };

    type Age = {
        id: number;
        icon: string | null;
        color: string | null;
    };

    const ageLabels = ['Juniorzy', 'Grupa I', 'Grupa II', 'Grupa III', 'Grupa IV'];

    let problemsQuery = createOdysejaQuery<(Problem | null)[]>({
        queryKey: ['problems'],
        path: '/api/problem',
    });

    let problems = $derived(
        (problemsQuery.data ?? []).filter((p): p is Problem => p != null),
    );

    let draftProblems = $state<Problem[]>([]);
    let initialSnapshot = $state('');

    $effect(() => {
        if (problems.length && !draftProblems.length) {
            draftProblems = problems.map((p) => ({id: p.id, name: p.name, icon: p.icon ?? null, color: p.color ?? null}));
            initialSnapshot = JSON.stringify(draftProblems);
        }
    });

    let isDirty = $derived(JSON.stringify(draftProblems) !== initialSnapshot);

    let saveProblemsMutation = createPutMutation<void, Problem[]>({
        path: '/api/problem',
        queryKey: ['problems'],
        onSuccess: () => {
            toast.success('Problemy zaktualizowane');
            initialSnapshot = JSON.stringify(draftProblems);
        },
    });

    function saveProblems() {
        saveProblemsMutation.mutate(draftProblems);
    }

    let agesQuery = createOdysejaQuery<Age[]>({
        queryKey: ['ages'],
        path: '/api/age',
    });

    let draftAges = $state<Age[]>([]);
    let agesSnapshot = $state('');

    $effect(() => {
        const ages = agesQuery.data;
        if (ages?.length && !draftAges.length) {
            draftAges = ages.map((a) => ({id: a.id, icon: a.icon ?? null, color: a.color ?? null}));
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
        },
    });

    onMount(() => {
        setBreadcrumbs([
            {name: 'Problemy', href: '/problems'},
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex flex-col gap-1">
        <div class="flex items-stretch gap-3 w-full">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconForms class="size-5 text-primary"/>
            </div>
            <div class="flex-1">
                <h1 class="text-2xl font-semibold tracking-tight">Problemy</h1>
            </div>
            <div class="flex flex-wrap items-center justify-end gap-2">
                {#if draftProblems.length > 0}
                    <Button
                            onclick={saveProblems}
                            disabled={!isDirty || saveProblemsMutation.isPending}
                    >
                        Zapisz zmiany
                    </Button>
                {/if}
            </div>
        </div>
    </div>

    {#if problemsQuery.error}
        <div class="text-red-500 mb-4">{String(problemsQuery.error)}</div>
    {:else if problemsQuery.isPending}
        <Spinner size="sm"/>
    {:else}

        {#if problems.length === 0}
            <div class="rounded-lg border border-dashed p-8 text-center text-muted-foreground">
                Na liście nie ma problemów.
            </div>
        {:else}
            <div class="rounded-md border overflow-x-auto">
                <Table.Root>
                    <Table.Header>
                        <Table.Row>
                            <Table.Head class="w-24">Id</Table.Head>
                            <Table.Head>Nazwa</Table.Head>
                            <Table.Head class="w-44">Kafelek w harmonogramie</Table.Head>
                        </Table.Row>
                    </Table.Header>

                    <Table.Body>
                        {#each draftProblems as problem (problem.id)}
                            <Table.Row>
                                <Table.Cell class="align-middle font-mono text-sm">{problem.id}</Table.Cell>
                                <Table.Cell class="align-middle">
                                    <Input bind:value={problem.name} class="max-w-xl"/>
                                </Table.Cell>
                                <Table.Cell class="align-middle">
                                    <TileStyleButton
                                            bind:icon={problem.icon}
                                            bind:color={problem.color}
                                            label={problem.name.replace(/\\n|\n/g, ' ') || `Problem ${problem.id}`}
                                            defaultIcon="puzzle-piece-01"
                                            defaultColorIndex={problem.id}
                                    />
                                </Table.Cell>
                            </Table.Row>
                        {/each}
                    </Table.Body>
                </Table.Root>
            </div>
        {/if}
    {/if}

    <div class="flex flex-col gap-4">
        <div class="flex items-center justify-between gap-3">
            <div>
                <h2 class="text-lg font-semibold tracking-tight">Grupy wiekowe</h2>
                <p class="text-sm text-muted-foreground">Wygląd kafelków grup wiekowych w harmonogramie aplikacji.</p>
            </div>
            {#if draftAges.length > 0}
                <Button
                        onclick={() => saveAgesMutation.mutate(draftAges)}
                        disabled={!agesDirty || saveAgesMutation.isPending}
                >
                    Zapisz zmiany
                </Button>
            {/if}
        </div>

        {#if agesQuery.error}
            <div class="text-red-500">{String(agesQuery.error)}</div>
        {:else if agesQuery.isPending}
            <Spinner size="sm"/>
        {:else}
            <div class="rounded-md border overflow-x-auto">
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
                                <Table.Cell class="align-middle">{ageLabels[age.id] ?? `Grupa ${age.id}`}</Table.Cell>
                                <Table.Cell class="align-middle">
                                    <TileStyleButton
                                            bind:icon={age.icon}
                                            bind:color={age.color}
                                            label={ageLabels[age.id] ?? `Grupa ${age.id}`}
                                            defaultIcon="users-01"
                                            defaultColorIndex={age.id}
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
