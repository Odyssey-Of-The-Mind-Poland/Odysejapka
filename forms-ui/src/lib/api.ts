/**
 * API client that routes all requests through the SvelteKit BFF proxy.
 * No tokens are handled client-side — the proxy reads them from
 * server-side encrypted cookies and forwards as Bearer headers.
 */
export async function apiFetch<T>(
	endpoint: string,
	options: RequestInit = {}
): Promise<T> {
	// Rewrite /api/... to /api/proxy/... so the BFF proxy handles it.
	// E.g. /api/v1/users/me → /api/proxy/v1/users/me
	const url = endpoint.startsWith('/api/')
		? `/api/proxy/${endpoint.slice(5)}`
		: endpoint;

	const headers = new Headers(options.headers || {});
	if (!headers.has('Content-Type')) {
		headers.set('Content-Type', 'application/json');
	}

	const response = await fetch(url, {
		...options,
		headers
	});

	if (!response.ok) {
		let errorMessage = `API error: ${response.status} ${response.statusText}`;
		try {
			const errorBody = await response.text();
			if (errorBody) {
				const parsed = JSON.parse(errorBody);
				if (parsed.message) {
					errorMessage = parsed.message;
				}
			}
		} catch {
			// keep default error message
		}
		throw new Error(errorMessage);
	}

	const text = await response.text();

	if (!text || !text.trim()) {
		return {} as T;
	}

	return JSON.parse(text) as T;
}
