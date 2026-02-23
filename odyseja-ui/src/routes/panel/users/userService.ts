import { getWithAuth, postNoCity } from '$lib/apiService';

export type User = {
    id: number;
    username: string;
    email: string;
    userId: string;
    roles: string[];
};

export async function fetchUsers(): Promise<User[]> {
    const data = await getWithAuth('/api/v1/users');
    return (data ?? []).map((u: { id: number; username: string; email: string; userId: string; roles: string[] }) => ({
        id: u.id,
        username: u.username,
        email: u.email,
        userId: u.userId,
        roles: u.roles ?? [],
    }));
}

export async function fetchUser(id: number): Promise<User | null> {
    const data = await getWithAuth(`/api/v1/users/${id}`);
    if (!data) return null;
    return {
        id: data.id,
        username: data.username,
        email: data.email,
        userId: data.userId,
        roles: data.roles ?? [],
    };
}

export async function fetchRoles(): Promise<string[]> {
    const data = await getWithAuth('/api/v1/roles');
    return (data ?? []).map((r: string) => r);
}

export async function updateUserRoles(userId: number, roles: string[]): Promise<void> {
    await postNoCity(roles, `/api/v1/users/${userId}/roles`, 'Role zaktualizowane');
}
