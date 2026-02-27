import type { Actions } from './$types';
import { redirect } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';
import { clearBackendToken } from '$lib/server/session';

export const actions: Actions = {
	default: async ({ cookies, url }) => {
		const hadAuth0Session = !!cookies.get('authjs.session-token') ||
			!!cookies.get('__Secure-authjs.session-token');

		// Clear local backend token
		clearBackendToken(cookies);

		// Clear Auth.js session cookies
		cookies.delete('authjs.session-token', { path: '/' });
		cookies.delete('__Secure-authjs.session-token', { path: '/' });
		cookies.delete('authjs.callback-url', { path: '/' });
		cookies.delete('authjs.csrf-token', { path: '/' });

		if (hadAuth0Session && env.AUTH0_ISSUER && env.AUTH0_CLIENT_ID) {
			// Redirect to Auth0's logout endpoint to end the Auth0 session too
			const returnTo = encodeURIComponent(url.origin);
			const logoutUrl = `${env.AUTH0_ISSUER}/v2/logout?client_id=${env.AUTH0_CLIENT_ID}&returnTo=${returnTo}`;
			throw redirect(302, logoutUrl);
		}

		throw redirect(302, '/');
	}
};
