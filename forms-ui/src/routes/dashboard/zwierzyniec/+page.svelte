<script lang="ts">
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {onMount} from "svelte";
    import IconTrees from "@tabler/icons-svelte/icons/trees";

    onMount(() => {
        setBreadcrumbs([
            {name: 'Zwierzyniec', href: '/dashboard/zwierzyniec'},
        ]);
    });
</script>

<div class="flex flex-col items-center justify-center py-16 gap-3">
    <div class="flex items-center justify-center size-12 rounded-lg bg-muted">
        <IconTrees class="size-6 text-muted-foreground"/>
    </div>
    <p class="text-muted-foreground font-medium">Wybierz miasto z panelu bocznego</p>
</div>
