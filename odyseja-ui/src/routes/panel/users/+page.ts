import type { PageLoad } from './$types';
import { fetchUsers } from './userService';

export const load = (() => {
    return fetchUsers().then((users) => ({ users })).catch(() => ({ users: [], fetchError: 'Nie udało się pobrać użytkowników.' }));
}) satisfies PageLoad;
