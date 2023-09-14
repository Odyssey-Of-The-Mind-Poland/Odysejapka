import {get} from '$lib/apiService';
import type { PageLoad } from './$types';
import type {Stage, Stages} from "$lib/types";

export const load = (({params}) => {
    return fetchStages();
}) satisfies PageLoad;


async function fetchStages(): Promise<Stages> {
    const data = await get('/stage')
    const stages = data as Stage[]
    return {stages: stages};
}
