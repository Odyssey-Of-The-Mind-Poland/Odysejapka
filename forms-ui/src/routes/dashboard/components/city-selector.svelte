<script lang="ts">
    import * as Select from '$lib/components/ui/select/index.js';
    import {createOdysejaQuery} from '$lib/queries';
    import {selectedCity, setCity, clearCity, type City} from '$lib/cityStore';
    import {goto} from '$app/navigation';
    import {page} from '$app/state';

    let citiesQuery = createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    });

    let cities = $derived.by(() => {
        if (!citiesQuery.data) return [];
        return [...citiesQuery.data].sort((a, b) => a.name.localeCompare(b.name));
    });

    let currentValue = $derived($selectedCity ? String($selectedCity.id) : undefined);

    function handleValueChange(value: string | undefined) {
        if (!value) {
            clearCity();
            return;
        }
        const city = cities.find(c => String(c.id) === value);
        if (!city) return;
        setCity(city);

        // Navigate to city-specific route if we're in a section that needs city
        const path = page.url.pathname;
        const sectionMatch = path.match(/^\/dashboard\/(competitions|lappka|zwierzyniec)/);
        if (sectionMatch) {
            const section = sectionMatch[1];
            const defaultChild: Record<string, string> = {
                competitions: 'dt',
                lappka: 'info',
                zwierzyniec: 'rak',
            };
            goto(`/dashboard/${section}/${city.id}/${defaultChild[section]}`);
        }
    }
</script>

<div class="px-3 py-2">
    <Select.Root type="single" value={currentValue} onValueChange={handleValueChange}>
        <Select.Trigger class="w-full">
            {$selectedCity?.name ?? 'Wybierz miasto...'}
        </Select.Trigger>
        <Select.Content>
            {#each cities as city (city.id)}
                <Select.Item value={String(city.id)} label={city.name}>
                    {city.name}
                </Select.Item>
            {/each}
        </Select.Content>
    </Select.Root>
</div>
