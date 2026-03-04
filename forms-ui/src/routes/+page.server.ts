import type { Actions, PageServerLoad } from './$types';
import { fail, redirect } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';
import { setBackendToken, getBackendToken } from '$lib/server/session';

const BACKEND_URL = env.BACKEND_URL || 'http://localhost:8081';

export const load: PageServerLoad = async ({ locals, cookies }) => {
	const auth0Session = await locals.auth();
	const localToken = getBackendToken(cookies);

	if (auth0Session || localToken) {
		throw redirect(302, '/dashboard');
	}
};

export const actions: Actions = {
	default: async ({ request, cookies }) => {
		const formData = await request.formData();
		const email = formData.get('email') as string;
		const password = formData.get('password') as string;

		if (!email || !password) {
			return fail(400, { error: 'Email and password are required', email });
		}

		try {
			const response = await fetch(`${BACKEND_URL}/api/v1/auth/login`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ email, password })
			});

			if (!response.ok) {
				return fail(401, { error: 'Invalid email or password', email });
			}

			const { token } = await response.json();
			setBackendToken(cookies, token);
		} catch (e) {
			console.error('Login error:', e);
			return fail(500, { error: 'Unable to reach the server. Please try again later.', email });
		}

		throw redirect(302, '/dashboard');
	}
};
