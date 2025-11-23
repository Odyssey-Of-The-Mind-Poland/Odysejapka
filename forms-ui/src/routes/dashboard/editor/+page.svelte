<script lang="ts">
    import {apiFetch} from "$lib/api";
    import {Spinner} from "$lib/components/ui/spinner";
    import {goto} from "$app/navigation";
    import * as Table from '$lib/components/ui/table/index.js';
    import {setBreadcrumbs} from "$lib/breadcrumbs";

    setBreadcrumbs([
        {name: 'Edytor Formularzy', href: '/dashboard/editor'}
    ]);

    type Problem = {
        id: number,
        name: string
    }

    let problemsPromise = apiFetch<Problem[]>('/api/problem')
</script>

{#await problemsPromise}
    <Spinner/>
{:then problems}
    <div class="rounded-md border overflow-x-auto">
        <Table.Root>
            <Table.Header>
                <Table.Row>
                    <Table.Head>Numer</Table.Head>
                    <Table.Head>Nazwa</Table.Head>
                </Table.Row>
            </Table.Header>

            <Table.Body>
                {#each problems as problem (problem.id)}
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
{:catch error}
    <p>Something went wrong: {error.message}</p>
{/await}