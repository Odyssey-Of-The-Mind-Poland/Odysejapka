<script lang="ts">
    import {apiFetch} from "$lib/api";
    import {Spinner} from "$lib/components/ui/spinner";
    import {page} from "$app/state";
    import {setBreadcrumbs} from "$lib/breadcrumbs";

    let problem = $derived(page.params.id);
    let formPromise = $derived(apiFetch(`/api/v1/form/${problem}`));

    $effect(() => {
        setBreadcrumbs([
            {name: 'Edytor Formularzy', href: '/dashboard/editor'},
            {name: `Problem ${problem}`, href: `/dashboard/editor/${problem}`}
        ]);
    })

</script>

{#await formPromise}
    <Spinner/>
{:then form}
    {JSON.stringify(form)}
{:catch error}
    <p>Something went wrong: {error.message}</p>
{/await}