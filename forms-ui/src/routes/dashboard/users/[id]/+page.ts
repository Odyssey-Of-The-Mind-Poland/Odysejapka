import type {LayoutServerLoad} from "../../../../../.svelte-kit/types/src/routes/dashboard/$types";
import {apiFetch} from "$lib/api";
import type {PageLoad} from './$types';

export const load: PageLoad = async ({params, parent}) => {
    const {session} = await parent();
    const user: User = await apiFetch(`/api/v1/users/${params.id}`, {
        apiToken: session?.accessToken
    });
    const roles = await apiFetch(`/api/v1/roles`, {
        apiToken: session?.accessToken
    });
    return {user, roles};
};
