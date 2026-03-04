<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery} from "$lib/queries";
    import {goto} from "$app/navigation";
    import {page} from "$app/state";
    import UsersIcon from "@lucide/svelte/icons/users";
    import RequirePermission from "$lib/components/RequirePermission.svelte";
    import CsvUploadDialog from "../CsvUploadDialog.svelte";

    type City = {
        id: number;
        name: string;
    };

    type PerformanceGroup = {
        group: {
            city: string;
            stage: number;
        };
        performances: Array<{
            city: string;
            stage: number;
        }>;
    };

    let cityId = $derived(Number(page.params.cityId));

    let citiesQuery = $derived(createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    }));

    let cityName = $derived.by(() => {
        if (!citiesQuery.data) return null;
        return citiesQuery.data.find((c: City) => c.id === cityId)?.name ?? null;
    });

    let performanceGroupsQuery = createOdysejaQuery<PerformanceGroup[]>({
        queryKey: ['dashboardTeams'],
        path: '/api/v1/dashboard/teams',
    });

    let stages = $derived.by(() => {
        if (!performanceGroupsQuery.data || !cityName) return [];
        const stageSet = new Set<number>();
        performanceGroupsQuery.data.forEach((group: PerformanceGroup) => {
            group.performances.forEach((p) => {
                if (p.city === cityName) stageSet.add(p.stage);
            });
        });
        return [...stageSet].sort((a, b) => a - b);
    });

    $effect(() => {
        if (stages.length > 0) {
            goto(`/dashboard/competitions/${cityId}/dt/${stages[0]}`, {replaceState: true});
        }
    });
</script>

{#if performanceGroupsQuery.isPending || citiesQuery.isPending}
    <div class="flex flex-col items-center justify-center py-16 gap-3">
        <Spinner size="sm"/>
        <p class="text-sm text-muted-foreground">Ładowanie...</p>
    </div>
{:else if performanceGroupsQuery.error}
    <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
        <p class="font-medium text-destructive">Błąd podczas ładowania</p>
    </div>
{:else if stages.length === 0}
    <div class="rounded-lg border border-dashed p-12 text-center">
        <UsersIcon class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
        <p class="text-muted-foreground font-medium">Brak drużyn</p>
        <p class="text-sm text-muted-foreground/70 mt-1">W tym mieście nie ma jeszcze drużyn.</p>
        <RequirePermission role="ADMINISTRATOR">
            <div class="mt-4">
                <CsvUploadDialog {cityId}/>
            </div>
        </RequirePermission>
    </div>
{/if}
