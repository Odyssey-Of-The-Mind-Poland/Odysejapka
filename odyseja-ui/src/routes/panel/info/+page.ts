import {fetchInfo} from '$lib/apiService';
import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/panel/problem/$types";

export const load = (({params}) => {
    return fetchInfo();
}) satisfies PageLoad;
