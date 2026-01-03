import { createQuery, createMutation, getQueryClientContext } from '@tanstack/svelte-query';
import { toast } from 'svelte-sonner';
import { apiFetch } from '$lib/api';

export function createTexapiQuery<TData>({
	queryKey,
	path,
	initialData = undefined,
	enabled = true
}: {
	queryKey: string[];
	path: string;
	initialData?: TData;
	enabled?: boolean;
}) {
	return createQuery<TData, Error>(() => ({
		queryKey,
		initialData,
		enabled: enabled,
		queryFn: async () => {
			const data = await apiFetch<TData>(path, {});
			return data as TData;
		}
	}));
}

export function createSingleTextapiQuery<TData>({
	queryKey,
	path
}: {
	queryKey: string[];
	path: string;
}) {
	return createQuery<TData, Error>(() => ({
		queryKey,
		queryFn: async () => {
			return await apiFetch<TData>(path, {});
		}
	}));
}

function createBodyMutation<TData = unknown, TVariables = unknown>({
	method,
	path,
	queryKey,
	onSuccess
}: {
	method: 'POST' | 'PUT' | 'PATCH' | 'DELETE';
	path: string | ((variables: TVariables) => string);
	queryKey: string[];
	onSuccess?: (data: TData) => void;
}) {
	const queryClient = getQueryClientContext();

	return createMutation<TData, Error, TVariables>(() => ({
		mutationFn: async (variables: TVariables) => {
			try {
				const target = resolvePath(path, variables);

				const hasBody = variables !== undefined && variables !== null;
				const body =
					hasBody && (variables as any).body === undefined ? variables : (variables as any).body;

				const response = await apiFetch<TData>(target, {
					method,
					...(hasBody && {
						headers: { 'Content-Type': 'application/json' },
						body: JSON.stringify(body)
					})
				});

				return response;
			} catch (err) {
				console.error('mutation error', err);
				throw err;
			}
		},
		onSuccess: (data) => {
			console.log('invalidate', queryKey);
			queryClient.invalidateQueries({ queryKey }).then(() => {
				onSuccess?.(data);
			});
		},
		onError: (error) => {
			toast.error(error.message);
		}
	}));
}

export const createPostMutation = <TData = unknown, TVariables = unknown>(opts: {
	path: string | ((variables: TVariables) => string);
	queryKey: string[];
	onSuccess?: (data: TData) => void;
}) => createBodyMutation<TData, TVariables>({ method: 'POST', ...opts });

export const createPutMutation = <TData = unknown, TVariables = unknown>(opts: {
	path: string | ((variables: TVariables) => string);
	queryKey: string[];
	onSuccess?: (data: TData) => void;
}) => createBodyMutation<TData, TVariables>({ method: 'PUT', ...opts });

export const createDelMutation = <TData = unknown, TVariables = unknown>(opts: {
	path: string | ((variables: TVariables) => string);
	queryKey: string[];
	onSuccess?: (data: TData) => void;
}) => createBodyMutation<TData, TVariables>({ method: 'DELETE', ...opts });

function resolvePath<TVariables>(
	path: string | ((variables: TVariables) => string),
	variables: TVariables
): string {
	if (typeof path === 'function') {
		return path(variables);
	}

	return path.replace(/{([^}]+)}/g, (_, key: string) => {
		const value = (variables as any)?.[key];

		if (value === undefined || value === null) {
			console.warn(`Missing path variable "${key}" in`, variables);
			return '';
		}

		return encodeURIComponent(String(value));
	});
}
