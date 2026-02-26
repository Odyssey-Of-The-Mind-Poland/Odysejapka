import type { RequestHandler } from '@sveltejs/kit';
import { redirect } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';
import { getBackendToken, clearBackendToken } from '$lib/server/session';

const BACKEND_URL = env.BACKEND_URL || 'http://localhost:8081';

async function proxyRequest(event: Parameters<RequestHandler>[0]): Promise<Response> {
	const path = event.params.path;
	const targetUrl = `${BACKEND_URL}/api/${path}${event.url.search}`;

	// Try local backend token first, then Auth0 token from @auth/sveltekit session
	let token = getBackendToken(event.cookies);

	if (!token) {
		const session = await event.locals.auth();
		token = (session as any)?.accessToken ?? null;
	}

	const headers = new Headers();

	// Forward Content-Type as-is (important for multipart/form-data boundaries)
	const contentType = event.request.headers.get('Content-Type');
	if (contentType) {
		headers.set('Content-Type', contentType);
	}

	headers.set('Accept', event.request.headers.get('Accept') || 'application/json');

	if (token) {
		headers.set('Authorization', `Bearer ${token}`);
	}

	const init: RequestInit = {
		method: event.request.method,
		headers
	};

	if (!['GET', 'HEAD'].includes(event.request.method)) {
		// Use arrayBuffer to support both text and binary (file upload) bodies
		init.body = await event.request.arrayBuffer();
	}

	const response = await fetch(targetUrl, init);

	// Forward the response with relevant headers
	const responseHeaders = new Headers();
	const respContentType = response.headers.get('Content-Type');
	if (respContentType) {
		responseHeaders.set('Content-Type', respContentType);
	}
	const contentDisposition = response.headers.get('Content-Disposition');
	if (contentDisposition) {
		responseHeaders.set('Content-Disposition', contentDisposition);
	}

	return new Response(response.body, {
		status: response.status,
		statusText: response.statusText,
		headers: responseHeaders
	});
}

export const GET: RequestHandler = proxyRequest;
export const POST: RequestHandler = proxyRequest;
export const PUT: RequestHandler = proxyRequest;
export const PATCH: RequestHandler = proxyRequest;
export const DELETE: RequestHandler = proxyRequest;
