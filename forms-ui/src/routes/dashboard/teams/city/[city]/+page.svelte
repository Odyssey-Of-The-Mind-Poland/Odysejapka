<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery} from "$lib/queries";
    import {goto} from "$app/navigation";
    import {page} from "$app/state";
    import UsersIcon from "@lucide/svelte/icons/users";

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

    let cityName = $derived(decodeURIComponent(page.params.city));

    let performanceGroupsQuery = createOdysejaQuery<PerformanceGroup[]>({
        queryKey: ['performanceGroups'],
        path: '/api/v2/timeTable',
    });

    let stages = $derived.by(() => {
        if (!performanceGroupsQuery.data) return [];
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
            goto(`/dashboard/teams/city/${encodeURIComponent(cityName)}/${stages[0]}`, {replaceState: true});
        }
    });
</script>

{#if performanceGroupsQuery.isPending}
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
    </div>
{/if}
