import type { PageLoad } from './$types';
import { get } from '$lib/apiService';
import type { SakRequest } from '$lib/types';

export const load = (async () => {
    return await get('/api/v1/sak') as SakRequest;
}) satisfies PageLoad;


