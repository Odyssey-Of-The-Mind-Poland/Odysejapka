import {fetchCities} from "./city/cityService";


export async function load() {
    const cities = await fetchCities();
    console.log(cities)
    return cities
}
