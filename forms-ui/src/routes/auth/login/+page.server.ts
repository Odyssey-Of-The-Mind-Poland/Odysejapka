import type { PageServerLoad } from './$types';
import { redirect } from '@sveltejs/kit';

export const load: PageServerLoad = async () => {
	// Login is now the main page at /
	throw redirect(302, '/');
};
