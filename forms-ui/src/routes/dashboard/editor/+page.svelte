<script lang="ts">
    import {apiFetch} from "$lib/api";
    import {Spinner} from "$lib/components/ui/spinner";
    import {goto} from "$app/navigation";
    import * as Table from '$lib/components/ui/table/index.js';
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {createOdysejaQuery} from "$lib/queries";

    setBreadcrumbs([
        {name: 'Edytor Formularzy', href: '/dashboard/editor'}
    ]);

    type Problem = {
        id: number,
        name: string
    }

    let problemsQuery = createOdysejaQuery<Problem[]>({
        queryKey: ['problems'],
        path: '/api/problem',
    });
</script>

{#if problemsQuery.error}
    <div class="text-red-500 mb-4">{String(problemsQuery.error)}</div>
{:else if problemsQuery.isPending}
    <Spinner size="sm"/>
{:else if problemsQuery?.data.length === 0}
    <div>No problems found.</div>
{:else}
    <div class="rounded-md border overflow-x-auto">
        <Table.Root>
            <Table.Header>
                <Table.Row>
                    <Table.Head>Numer</Table.Head>
                    <Table.Head>Nazwa</Table.Head>
                </Table.Row>
            </Table.Header>

            <Table.Body>
                {#each problemsQuery.data as problem (problem.id)}
                    <Table.Row
                            class="cursor-pointer hover:bg-muted/50"
                            onclick={() => goto(`/dashboard/editor/${problem.id}`)}
                    >
                        <Table.Cell>{problem.id}</Table.Cell>
                        <Table.Cell>{problem.name}</Table.Cell>
                    </Table.Row>
                {/each}
            </Table.Body>
        </Table.Root>
    </div>
{/if}