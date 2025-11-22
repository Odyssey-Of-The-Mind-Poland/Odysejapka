<script lang="ts">
    import {apiFetch} from "$lib/api";
    import {Spinner} from "$lib/components/ui/spinner";
    import {page} from "$app/state";

    let formPromise = $derived(apiFetch(`/api/v1/form/${page.params.id}`));
</script>

{#await formPromise}
    <Spinner/>
{:then form}
    {JSON.stringify(form)}
{:catch error}
    <p>Something went wrong: {error.message}</p>
{/await}