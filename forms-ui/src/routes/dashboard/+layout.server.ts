import type {LayoutServerLoad} from './$types';
import {redirect} from '@sveltejs/kit';
import {getBackendToken} from '$lib/server/session';

export const load: LayoutServerLoad = async ({locals, cookies}) => {
	const auth0Session = await locals.auth();
	const localToken = getBackendToken(cookies);

	if (!auth0Session && !localToken) {
		throw redirect(302, '/');
	}

	return {};
};
