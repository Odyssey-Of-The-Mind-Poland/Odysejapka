import type {Stage, Stages} from "$lib/types";
import {get} from "$lib/apiService";

export async function fetchStages(): Promise<Stages> {
    const data = await get('/stage')
    const stages = data as Stage[]
    return {stages: stages};
}