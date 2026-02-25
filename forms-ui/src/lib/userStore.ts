import { writable } from 'svelte/store';
import type { Writable } from 'svelte/store';

export type Role = 'ADMINISTRATOR' | 'USER';

export type CurrentUser = {
	id: number;
	username: string;
	email: string;
	userId: string;
	roles: Role[];
};

export const currentUser: Writable<CurrentUser | null> = writable(null);
