import { fetchStages } from '../apiService';
import type { PageLoad } from './$types';

export const load = (({params}) => {
    return fetchStages();
}) satisfies PageLoad;
