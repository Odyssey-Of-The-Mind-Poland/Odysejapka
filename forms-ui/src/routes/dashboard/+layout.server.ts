import type { LayoutServerLoad } from './$types';
import { redirect } from '@sveltejs/kit';
import { getBackendToken } from '$lib/server/session';

export const load: LayoutServerLoad = async ({ locals, cookies }) => {
	const auth0Session = await locals.auth();
	const localToken = getBackendToken(cookies);

	// Require either an Auth0 session or a local backend token
	if (!auth0Session && !localToken) {
		throw redirect(302, '/auth/login');
	}

	return {};
};
