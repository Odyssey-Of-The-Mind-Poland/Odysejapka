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

    type Problem = {
        id: number;
        name: string;
    };

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
            draftProblems = problems.map((p) => ({id: p.id, name: p.name}));
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

    onMount(() => {
        setBreadcrumbs([
            {name: 'Problemy', href: '/problems'},
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex flex-col gap-1">
        <div class="flex items-center gap-3">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconForms class="size-5 text-primary"/>
            </div>
            <div>
                <h1 class="text-2xl font-semibold tracking-tight">Problemy</h1>
                <p class="text-sm text-muted-foreground">
                    Słownik globalny (nie jest przypisany do miasta).
                    API <span class="font-medium text-foreground">ProblemController</span>:
                    <code class="text-xs bg-muted px-1 py-0.5 rounded">GET</code> lista,
                    <code class="text-xs bg-muted px-1 py-0.5 rounded">PUT</code> nazwy.
                </p>
            </div>
        </div>
    </div>

    {#if problemsQuery.error}
        <div class="text-red-500 mb-4">{String(problemsQuery.error)}</div>
    {:else if problemsQuery.isPending}
        <Spinner size="sm"/>
    {:else}
        <div class="flex flex-wrap items-center justify-end gap-2">
            {#if draftProblems.length > 0}
                <Button
                        onclick={saveProblems}
                        disabled={!isDirty || saveProblemsMutation.isPending}
                >
                    Zapisz zmiany (PUT)
                </Button>
            {/if}
        </div>

        {#if problems.length === 0}
            <div class="rounded-lg border border-dashed p-8 text-center text-muted-foreground">
                Na liście nie ma problemów (<code class="text-xs">GET /api/problem</code> zwróciło pustą listę).
            </div>
        {:else}
            <div class="rounded-md border overflow-x-auto">
                <Table.Root>
                    <Table.Header>
                        <Table.Row>
                            <Table.Head class="w-24">Id</Table.Head>
                            <Table.Head>Nazwa</Table.Head>
                        </Table.Row>
                    </Table.Header>

                    <Table.Body>
                        {#each draftProblems as problem (problem.id)}
                            <Table.Row>
                                <Table.Cell class="align-middle font-mono text-sm">{problem.id}</Table.Cell>
                                <Table.Cell class="align-middle">
                                    <Input bind:value={problem.name} class="max-w-xl"/>
                                </Table.Cell>
                            </Table.Row>
                        {/each}
                    </Table.Body>
                </Table.Root>
            </div>
        {/if}
    {/if}
</div>
