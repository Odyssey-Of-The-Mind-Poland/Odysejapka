import { fetchStages } from '$lib/apiService';
import type { PageLoad } from './$types';

export const load = (({params}) => {
    return fetchStages();
}) satisfies PageLoad;
