import { session } from './sessionStore';
import { get } from 'svelte/store';

export const baseUrl = import.meta.env.VITE_API_BASE_URL;

export async function apiFetch<T>(
	endpoint: string,
	{ apiToken = undefined, ...options }: RequestInit & { apiToken?: string } = {}
): Promise<T> {
	if (!baseUrl) {
		throw new Error('VITE_API_BASE_URL is not defined in environment variables');
	}

	const currentSession = get(session);
	const token = apiToken ?? currentSession?.accessToken;

	const headers = new Headers(options.headers || {});
	headers.set('Content-Type', 'application/json');
	if (token) {
		headers.set('Authorization', `Bearer ${token}`);
	}

	const url =
		endpoint.startsWith('http') || typeof window !== 'undefined' ? endpoint : baseUrl + endpoint;
	console.log(url);
	const response = await fetch(url, {
		...options,
		headers
	});

	if (!response.ok) {
		throw new Error(`API error: ${response.status} ${response.statusText}`);
	}
	const text = await response.text();

	if (!text || !text.trim()) {
		return {} as T;
	}

	return JSON.parse(text) as T;
}
