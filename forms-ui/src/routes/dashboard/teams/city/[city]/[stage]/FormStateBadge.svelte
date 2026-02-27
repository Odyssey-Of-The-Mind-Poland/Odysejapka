<script lang="ts">
    import {Badge} from "$lib/components/ui/badge/index.js";

    const { formState, formStateLabel } = $props<{
        formState: string;
        formStateLabel: string;
    }>();
</script>

{#if formState === 'APPROVED'}
    <Badge class="font-mono bg-green-600 text-white hover:bg-green-700">{formStateLabel}</Badge>
{:else if formState === 'ERROR'}
    <Badge variant="destructive" class="font-mono">{formStateLabel}</Badge>
{:else if formState === 'ANOMALY'}
    <Badge class="font-mono bg-amber-500 text-white hover:bg-amber-600">{formStateLabel}</Badge>
{:else if formState === 'SCORED'}
    <Badge variant="secondary" class="font-mono">{formStateLabel}</Badge>
{:else}
    <Badge variant="outline" class="font-mono text-muted-foreground">{formStateLabel}</Badge>
{/if}
