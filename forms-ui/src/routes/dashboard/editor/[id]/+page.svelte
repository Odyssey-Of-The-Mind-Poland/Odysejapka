<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {page} from "$app/state";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {Button} from "$lib/components/ui/button";
    import {toast} from "svelte-sonner";
    import * as Separator from "$lib/components/ui/separator/index.js";
    import {defaultEntry, type FormEntryType, type ProblemForm} from "./types";
    import {recalculateSortIndexes} from "./sortIndexUtils";
    import ScoringCard from "./ScoringCard.svelte";
    import StyleCard from "./StyleCard.svelte";
    import PenaltyCard from "./PenaltyCard.svelte";

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

    function addEntry(category: 'dtEntries' | 'styleEntries' | 'penaltyEntries', type: 'SCORING' | 'SECTION' | 'SCORING_GROUP' | 'STYLE' | 'PENALTY') {
        const newEntry: FormEntryType = defaultEntry(type)
        form = {
            ...(form ?? {dtEntries: [], styleEntries: [], penaltyEntries: []}),
            [category]: recalculateSortIndexes([...(form?.[category] ?? []), newEntry]),
        } as ProblemForm;
    }

    function removeEntry(category: 'dtEntries' | 'styleEntries' | 'penaltyEntries', index: number) {
        if (form) {
            form[category] = recalculateSortIndexes(form[category].filter((_, i) => i !== index));
        }
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
{:else if formQuery.isPending || !form}
    <Spinner size="sm"/>
{:else}
    <div class="flex flex-col gap-6 p-6">
        <div class="flex justify-between items-center">
            <h1 class="text-2xl font-bold">Edytor Formularza - Problem {problem}</h1>
            <Button onclick={save}>Zapisz</Button>
        </div>

        <Separator.Root/>

        <div class="flex flex-col gap-6">
            <ScoringCard
                    title="Punktacja długoterminowa"
                    entries={form.dtEntries}
                    bind:form={form}
                    onAddEntry={addEntry}
                    onRemoveEntry={removeEntry}
            />

            <StyleCard
                    title="Styl"
                    entries={form.styleEntries}
                    bind:form={form}
                    onAddEntry={addEntry}
                    onRemoveEntry={removeEntry}
            />

            <PenaltyCard
                    title="Karne"
                    entries={form.penaltyEntries}
                    bind:form={form}
                    onAddEntry={addEntry}
                    onRemoveEntry={removeEntry}
            />
        </div>
    </div>
{/if}