import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/panel/problem/$types";
import {get} from "$lib/apiService";
import type {GadRequest} from "$lib/types";

export const load = (async ({params}) => {
    return await get('/api/v1/gad') as GadRequest;
}) satisfies PageLoad;