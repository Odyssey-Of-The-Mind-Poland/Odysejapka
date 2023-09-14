import {get} from "$lib/apiService";
import type {PageLoad} from "../../../../../../.svelte-kit/types/src/routes/panel/info/$types";
import type {Info} from "$lib/types";


export const load = (({params}) => {
    console.log('params', params)
    // @ts-ignore
    const { id } = params;
    return fetchSingleInfo(id);
}) satisfies PageLoad;

async function fetchSingleInfo(id: number): Promise<Info> {
    return await get('/info/id/' + id) as Info;
}
