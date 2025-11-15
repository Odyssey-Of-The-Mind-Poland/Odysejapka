import type {LayoutServerLoad} from "../../../../.svelte-kit/types/src/routes/dashboard/$types";
import {apiFetch} from "$lib/api";

export const load: LayoutServerLoad = async ({parent}) => {
    const {session} = await parent();
    try {
        const users = await apiFetch(`/api/v1/users`, {
            apiToken: session?.accessToken
        });
        return {users};
    } catch (e) {
        console.log(e)
        return {
            users: [],
            fetchError: 'Network error while fetching users.',
        };
    }
};
