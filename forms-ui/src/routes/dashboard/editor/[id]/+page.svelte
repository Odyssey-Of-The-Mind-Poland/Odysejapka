<script lang="ts">
    import * as Table from '$lib/components/ui/table/index.js';
    import * as Card from '$lib/components/ui/card/index.js';
    import {Spinner} from "$lib/components/ui/spinner";
    import {page} from "$app/state";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import FormEntry from "./FormEntry.svelte";
    import {Button} from "$lib/components/ui/button";
    import {toast} from "svelte-sonner";


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
    let form = $state<ProblemForm>()
    $effect(() => {
        setBreadcrumbs([
            {name: 'Edytor Formularzy', href: '/dashboard/editor'},
            {name: `Problem ${problem}`, href: `/dashboard/editor/${problem}`}
        ]);
        form = formQuery.data;
    })

    function addDtEntry() {
        const newEntry: FormEntry = {id: Date.now(), name: '', calcType: CalcType.SUM};
        form = {
            ...(form ?? {dtEntries: [], styleEntries: [], penaltyEntries: []}),
            dtEntries: [...(form?.dtEntries ?? []), newEntry],
        } as ProblemForm;
    }

    let saveMutation = createPutMutation<ProblemForm>({
        path: `/api/v1/form/${problem}`,
        queryKey: ['problems'],
        onSuccess: data => {
            toast.success("Formularz zapisany pomyślnie");
        }
    })

    function save() {
        saveMutation.mutate(form)
    }

</script>

{#if formQuery.error}
    <div class="text-red-500 mb-4">{String(formQuery.error)}</div>
{:else if formQuery.isPending}
    <Spinner size="sm"/>
{:else}
    <Button onclick={save}>Zapisz</Button>
    <div class="flex flex-col">
        <h4>Punktacja długoterminowa</h4>
        <Card.Root class="gap-1">
            {#each form?.dtEntries as entry (entry.id)}
                <FormEntry id={entry.id} bind:name={entry.name}/>
            {/each}
            <Button variant="default" class="w-fit justify-start" onclick={addDtEntry}>Dodaj Kategorie</Button>
        </Card.Root>
    </div>
{/if}