import type {PageLoad} from './$types';
import {fetchStages} from "./stageService";

export const load = (({params}) => {
    return fetchStages();
}) satisfies PageLoad;


