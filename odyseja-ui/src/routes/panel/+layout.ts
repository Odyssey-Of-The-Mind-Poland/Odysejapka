import {fetchCities} from "$lib/apiService";


export async function load() {
    const cities = await fetchCities();
    console.log(cities)
    return cities
}

