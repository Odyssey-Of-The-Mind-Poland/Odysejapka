import type {PageLoad} from './$types';
import {fetchCities} from "./cityService";

export const load = (({params}) => {
    return fetchCities();
}) satisfies PageLoad;
