import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/panel/city/$types";
import {fetchBreakingChange} from "./breakingChangeService";


export const load = (async ({params}) => {
    return await fetchBreakingChange();
}) satisfies PageLoad;


