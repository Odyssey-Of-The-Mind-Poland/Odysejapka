<script lang="ts">
    import * as Table from '$lib/components/ui/table/index.js';
    import {Spinner} from "$lib/components/ui/spinner";
    import {page} from "$app/state";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {createOdysejaQuery} from "$lib/queries";


    type ProblemForm = {
        dtEntries: FormEntry[],
        styleEntries: FormEntry[],
        penaltyEntries: FormEntry[],
    }

    type FormEntry = {
        id: number,
        name: string
        calcType: CalcType
    }

    const CalcType = {
        SUM: 'SUM',
        AVERAGE: 'AVERAGE'
    } as const;

    type CalcType = typeof CalcType[keyof typeof CalcType];

    let problem = $derived(page.params.id);
    let formQuery = createOdysejaQuery<ProblemForm>({
        queryKey: ['problems'],
        path: `/api/v1/form/${problem}`,
    });
    $effect(() => {
        setBreadcrumbs([
            {name: 'Edytor Formularzy', href: '/dashboard/editor'},
            {name: `Problem ${problem}`, href: `/dashboard/editor/${problem}`}
        ]);
    })

</script>

{#if formQuery.error}
    <div class="text-red-500 mb-4">{String(formQuery.error)}</div>
{:else if formQuery.isPending}
    <Spinner size="sm"/>
{:else}
    <div class="rounded-md border overflow-x-auto">
        <Table.Root>
            <Table.Header>
                <Table.Row>
                    <Table.Head>ID</Table.Head>
                    <Table.Head>Nazwa</Table.Head>
                    <Table.Head>Typ Kalkulacji</Table.Head>
                </Table.Row>
            </Table.Header>

            <Table.Body>
                {#each formQuery.data.dtEntries as entry (entry.id)}
                    <Table.Row>
                        <Table.Cell>{entry.id}</Table.Cell>
                        <Table.Cell>{entry.name}</Table.Cell>
                        <Table.Cell>{entry.calcType}</Table.Cell>
                    </Table.Row>
                {/each}
            </Table.Body>
        </Table.Root>
    </div>
{/if}