import type {Cities, City} from "$lib/types";
import {get} from "$lib/apiService";

export async function fetchCities(): Promise<Cities> {
    const data = await get('/city');
    const cites = data as City[]
    return {cities: cites} as Cities;
}