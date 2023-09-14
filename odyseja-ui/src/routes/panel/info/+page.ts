import {get} from '$lib/apiService';
import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/panel/problem/$types";
import type {Info, InfoCategory, Infos} from "$lib/types";

export const load = (({params}) => {
    return fetchInfo();
}) satisfies PageLoad;


async function fetchInfo(): Promise<Infos> {
    const data = await get('/info');
    const categories = await fetchInfoCategory();
    return {infos: data as Info[], categories: categories} as Infos;
}

async function fetchInfoCategory(): Promise<InfoCategory[]> {
    const data = await get('/info/category');
    return data as InfoCategory[];
}