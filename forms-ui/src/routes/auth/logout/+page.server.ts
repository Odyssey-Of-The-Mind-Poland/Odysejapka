import type { Actions } from './$types';
import { redirect } from '@sveltejs/kit';
import { clearBackendToken } from '$lib/server/session';

export const actions: Actions = {
	default: async ({ cookies }) => {
		clearBackendToken(cookies);
		throw redirect(302, '/');
	}
};
