import { browser } from '$app/environment';
import { goto } from '$app/navigation';
import { env } from '$env/dynamic/public';

export const FRONTEND_URL = env.PUBLIC_FRONTEND_URL || 'http://localhost:5173';
const CLIENT_ID = '8TI8RllRK5wf5l1Rv85msCTOF0e88lZg';

let client: any;

async function getClient() {
	if (!browser) return null; // SSR guard

	if (!client) {
		const auth0 = await import('auth0-js'); // CommonJS-friendly
		const WebAuth = (auth0 as any).WebAuth ?? (auth0 as any).default?.WebAuth;

		client = new WebAuth({
			clientID: CLIENT_ID,
			domain: 'odyseja.eu.auth0.com',
			responseType: 'token id_token',
			audience: 'https://app.odyseja.org',
			redirectUri: `${FRONTEND_URL}/callback`,
			scope: 'openid profile'
		});
	}

	return client;
}

export async function login() {
	const c = await getClient();
	c?.authorize();
}

export async function logout() {
	const c = await getClient();
	c?.logout({
		returnTo: FRONTEND_URL,
		clientID: CLIENT_ID
	});

	setCookie('access_token', '');
	await goto('/');
}

export async function handleAuthentication() {
	const c = await getClient();

	return new Promise((resolve, reject) => {
		c.parseHash((error: any, authResult: any) => {
			if (authResult?.accessToken && authResult?.idToken) {
				setCookie('access_token', authResult.accessToken);
				resolve(authResult);
			} else if (error) {
				reject(error);
			}
		});
	});
}

function setCookie(name: string, value: string) {
	document.cookie = `${name}=${value}`;
}
