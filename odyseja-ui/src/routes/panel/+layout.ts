import {get} from "$lib/apiService";
import type {Cities, City} from "$lib/types";


export async function load() {
    const cities = await fetchCities();
    console.log(cities)
    return cities
}

async function fetchCities(): Promise<Cities> {
    const data = await get('/city');
    const cites = data as City[]
    return {cities: cites} as Cities;
}
