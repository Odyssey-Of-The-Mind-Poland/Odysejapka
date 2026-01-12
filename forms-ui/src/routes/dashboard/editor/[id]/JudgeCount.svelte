<script lang="ts">
    import {createOdysejaQuery} from "$lib/queries";
    import type {City} from "./types";
    import JudgeCountSelect from "./JudgeCountSelect.svelte";
    import {Spinner} from "$lib/components/ui/spinner";

    let cityQuery = createOdysejaQuery<City[]>({
        queryKey: ['city'],
        path: `/api/v1/city`,
    });

    let smallTeamCities = $state<string[]>([]);
    let bigTeamCities = $state<string[]>([]);

    function getAvailableCities(selectedInOther: string[]): City[] {
        if (!cityQuery.data) return [];
        return cityQuery.data.filter(city => !selectedInOther.includes(city.name));
    }

    function handleSmallTeamChange(selected: string[]) {
        smallTeamCities = selected;
    }

    function handleBigTeamChange(selected: string[]) {
        bigTeamCities = selected;
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
            selectedCities={smallTeamCities}
            availableCities={getAvailableCities(bigTeamCities)}
            onSelectionChange={handleSmallTeamChange}
        />
        <JudgeCountSelect 
            id="big" 
            title="Duży zespół"
            judgeCount={2}
            selectedCities={bigTeamCities}
            availableCities={getAvailableCities(smallTeamCities)}
            onSelectionChange={handleBigTeamChange}
        />
    </div>
{/if}