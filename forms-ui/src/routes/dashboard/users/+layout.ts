import {apiFetch} from "$lib/api";
import type {LayoutLoad} from './$types';

export const load: LayoutLoad = async () => {
    try {
        const users = await apiFetch(`/api/v1/users`);
        return {users};
    } catch (e) {
        console.log(e)
        return {
            users: [],
            fetchError: 'Network error while fetching users.',
        };
    }
};
