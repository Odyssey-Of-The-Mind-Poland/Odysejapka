<script lang="ts">
    import {createOdysejaQuery} from "$lib/queries";
    import type {City, ProblemForm} from "./types";
    import JudgeCountSelect from "./JudgeCountSelect.svelte";
    import {Spinner} from "$lib/components/ui/spinner";

    const {form = $bindable()} = $props<{form: ProblemForm}>();

    let cityQuery = createOdysejaQuery<City[]>({
        queryKey: ['city'],
        path: `/api/v1/city`,
    });

    let smallTeamCityIds = $state<number[]>(form.smallJudgesTeam ?? []);
    let bigTeamCityIds = $state<number[]>(form.bigJudgesTeam ?? []);

    $effect(() => {
        smallTeamCityIds = form.smallJudgesTeam ?? [];
        bigTeamCityIds = form.bigJudgesTeam ?? [];
    });

    function getAvailableCities(selectedInOther: number[]): City[] {
        if (!cityQuery.data) return [];
        return cityQuery.data.filter(city => !selectedInOther.includes(city.id));
    }

    function handleSmallTeamChange(selected: number[]) {
        smallTeamCityIds = selected;
        form.smallJudgesTeam = selected.length > 0 ? selected : null;
    }

    function handleBigTeamChange(selected: number[]) {
        bigTeamCityIds = selected;
        form.bigJudgesTeam = selected.length > 0 ? selected : null;
    }
</script>

{#if cityQuery.error}
    <div class="text-red-500 mb-4">{String(cityQuery.error)}</div>
{:else if cityQuery.isPending || !cityQuery.data }
    <Spinner size="sm"/>
{:else}
    <div class="w-full flex gap-4">
        <JudgeCountSelect 
            id="small" 
            title="Mały zespół"
            judgeCount={1}
            selectedCityIds={smallTeamCityIds}
            availableCities={getAvailableCities(bigTeamCityIds)}
            onSelectionChange={handleSmallTeamChange}
        />
        <JudgeCountSelect 
            id="big" 
            title="Duży zespół"
            judgeCount={2}
            selectedCityIds={bigTeamCityIds}
            availableCities={getAvailableCities(smallTeamCityIds)}
            onSelectionChange={handleBigTeamChange}
        />
    </div>
{/if}