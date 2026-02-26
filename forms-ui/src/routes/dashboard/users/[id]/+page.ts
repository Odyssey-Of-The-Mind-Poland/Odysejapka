import {apiFetch} from "$lib/api";
import type {PageLoad} from './$types';

export const load: PageLoad = async ({params}) => {
    const user: User = await apiFetch(`/api/v1/users/${params.id}`);
    const roles = await apiFetch(`/api/v1/roles`);
    return {user, roles};
};
