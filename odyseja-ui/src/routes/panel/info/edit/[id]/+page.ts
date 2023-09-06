import {fetchSingleInfo} from "../../../apiService";
import type {PageLoad} from "../../../../../../.svelte-kit/types/src/routes/panel/info/$types";


export const load = (({params}) => {
    console.log('params', params)
    // @ts-ignore
    const { id } = params;
    return fetchSingleInfo(id);
}) satisfies PageLoad;
