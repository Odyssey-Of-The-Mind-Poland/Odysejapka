import { writable } from 'svelte/store';
import type { Writable } from 'svelte/store';

export type Role = 'ADMINISTRATOR' | 'USER' | 'PROBLEM_1' | 'PROBLEM_2' | 'PROBLEM_3' | 'PROBLEM_4' | 'PROBLEM_5' | 'KAPITAN' | 'SPONTAN' | 'LAPPKA';

export type CurrentUser = {
	id: number;
	username: string;
	email: string;
	userId: string;
	roles: Role[];
};

export const currentUser: Writable<CurrentUser | null> = writable(null);
