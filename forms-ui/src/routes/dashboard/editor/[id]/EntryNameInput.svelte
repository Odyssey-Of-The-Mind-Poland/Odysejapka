<script lang="ts">
    import InputWithLabel from '$lib/components/form/InputWithLabel.svelte';

    interface Props {
        value: string;
        id?: string | number | null;
    }

    let {value = $bindable(), id}: Props = $props();
</script>

<InputWithLabel label="Nazwa" bind:value={value} {id} />

