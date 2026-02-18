import type { PageLoad } from './$types';
import { get } from '$lib/apiService';
import type { ZspIdRequest } from '$lib/types';

export const load = (async () => {
    return await get('/api/v1/rak') as ZspIdRequest;
}) satisfies PageLoad;


