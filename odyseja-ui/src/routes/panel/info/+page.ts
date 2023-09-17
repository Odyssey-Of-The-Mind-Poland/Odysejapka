import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/panel/problem/$types";
import {fetchInfo} from "./infoService";

export const load = (({params}) => {
    return fetchInfo();
}) satisfies PageLoad;