import {get} from "$lib/apiService";
import type {BreakingChange} from "$lib/types";

export async function fetchBreakingChange(): Promise<BreakingChange> {
    const data = await get('/breaking/change')
    return data as BreakingChange;
}