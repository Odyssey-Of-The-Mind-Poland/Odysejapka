<script lang="ts">
    import {page} from "$app/state";
    import {goto} from "$app/navigation";
    import {createOdysejaQuery} from "$lib/queries";
    import {Button} from "$lib/components/ui/button/index.js";
    import RequirePermission from "$lib/components/RequirePermission.svelte";
    import GenerateResultsDialog from "./GenerateResultsDialog.svelte";
    import IconForms from "@tabler/icons-svelte/icons/forms";
    import IconBulb from "@tabler/icons-svelte/icons/bulb";
    import {currentUser} from "$lib/userStore";

    type City = {
        id: number;
        name: string;
    };

    let cityId = $derived(Number(page.params.cityId));
    let isSpontan = $derived($currentUser?.roles.includes('SPONTAN') ?? false);

    let citiesQuery = $derived(createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    }));

    let cityName = $derived.by(() => {
        if (!citiesQuery.data) return '...';
        return citiesQuery.data.find((c: City) => c.id === cityId)?.name ?? '...';
    });

    let activeTab = $derived.by(() => {
        const path = page.url.pathname;
        if (path.includes('/spontany')) return 'spontany';
        return 'dt';
    });

    $effect(() => {
        if (isSpontan && !page.url.pathname.includes('/spontany')) {
            goto(`/dashboard/competitions/${cityId}/spontany`, {replaceState: true});
        }
    });

</script>

<div class="flex flex-col gap-4 h-full">
    <div class="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div class="flex gap-1 overflow-x-auto rounded-lg bg-muted p-1 w-full sm:w-fit [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
            {#if !isSpontan}
                <button
                        class="flex-1 whitespace-nowrap rounded-md px-3 py-2 text-sm font-medium transition-colors sm:flex-none sm:py-1.5 {activeTab === 'dt' ? 'bg-background shadow-sm' : 'text-muted-foreground hover:text-foreground'}"
                        onclick={() => goto(`/dashboard/competitions/${cityId}/dt`)}
                >
                    Problemy DT
                </button>
            {/if}
            <button
                    class="flex-1 whitespace-nowrap rounded-md px-3 py-2 text-sm font-medium transition-colors sm:flex-none sm:py-1.5 {activeTab === 'spontany' ? 'bg-background shadow-sm' : 'text-muted-foreground hover:text-foreground'}"
                    onclick={() => goto(`/dashboard/competitions/${cityId}/spontany`)}
            >
                Spontany
            </button>
        </div>
        <!-- RequirePermission renders no wrapper, so these target the buttons themselves. -->
        <div class="flex items-center gap-2 [&>*]:flex-1 sm:[&>*]:flex-none">
            <RequirePermission role="ADMINISTRATOR">
                {#if activeTab === 'dt'}
                    <Button variant="outline" size="sm" onclick={() => goto('/dashboard/editor')}>
                        <IconForms class="size-4 mr-1.5"/>
                        Edytor formularzy
                    </Button>
                {:else}
                    <Button variant="outline" size="sm" onclick={() => goto('/dashboard/spontan-editor')}>
                        <IconBulb class="size-4 mr-1.5"/>
                        Edytor spontanów
                    </Button>
                {/if}
                <GenerateResultsDialog {cityId}/>
            </RequirePermission>
        </div>
    </div>

    <slot/>
</div>
