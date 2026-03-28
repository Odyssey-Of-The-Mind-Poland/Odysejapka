import { apiFetch } from '$lib/api';

export type PunctationCells = {
	dt: string;
	style: string;
	penalty: string;
	balsa: string;
	anomaly: string;
	anomalyVerify: string;
	actualPerformanceStartTime?: string;
};

export type GadRequest = {
	templatesFolderId: string;
	destinationFolderId: string;
	zspId: string;
	problemPunctuationCells: Record<number, PunctationCells>;
};

export type SakRequest = {
	templatesFolderId: string;
	zspId: string;
};

export type LogLevel = 'INFO' | 'ERROR';

export type LogEntry = {
	logTime: string;
	message: string;
	level?: LogLevel;
};

export type Progress = {
	status: 'STOPPED' | 'RUNNING' | 'CANCELLED' | 'FAILED';
	progress: number;
	logs?: LogEntry[];
};

export type ReplacementCell = {
	sheetName: string;
	cell: string;
	value: string;
};

export type FixSheetCommand = {
	folderId: string;
	pattern: string;
	cells: ReplacementCell[];
};

function apiPath(path: string, params?: Record<string, string>): string {
	const base = path.startsWith('/api/') ? path : `/api/${path}`;
	if (params && Object.keys(params).length > 0) {
		const search = new URLSearchParams(params);
		return `${base}?${search.toString()}`;
	}
	return base;
}

function proxyPath(path: string, params?: Record<string, string>): string {
	const base = path.startsWith('/api/') ? `/api/proxy/${path.slice(5)}` : `/api/proxy/${path}`;
	if (params && Object.keys(params).length > 0) {
		const search = new URLSearchParams(params);
		return `${base}?${search.toString()}`;
	}
	return base;
}

export async function getRakInitial(cityId: number): Promise<{ zspId: string; contestName?: string }> {
	return apiFetch(apiPath('/api/v1/rak', { cityId: String(cityId) }));
}

export async function downloadRakPdf(
	cityId: number,
	zspId: string,
	isRegion: boolean,
	contestName?: string
): Promise<ArrayBuffer> {
	const params: Record<string, string> = { cityId: String(cityId) };
	if (isRegion) params.isRegion = 'true';
	const url = proxyPath('/api/v1/rak/download-pdf', params);
	const res = await fetch(url, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ zspId, contestName: contestName ?? null })
	});
	if (!res.ok) throw new Error(`HTTP ${res.status}`);
	return res.arrayBuffer();
}

export async function downloadRakShortPdf(
	cityId: number,
	zspId: string,
	isRegion: boolean,
	contestName?: string
): Promise<ArrayBuffer> {
	const params: Record<string, string> = { cityId: String(cityId) };
	if (isRegion) params.isRegion = 'true';
	const url = proxyPath('/api/v1/rak/download-short-latex-pdf', params);
	const res = await fetch(url, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ zspId, contestName: contestName ?? null })
	});
	if (!res.ok) throw new Error(`HTTP ${res.status}`);
	return res.arrayBuffer();
}

export async function downloadRakLatexPdf(
	cityId: number,
	zspId: string,
	isRegion: boolean,
	contestName?: string
): Promise<ArrayBuffer> {
	const params: Record<string, string> = { cityId: String(cityId) };
	if (isRegion) params.isRegion = 'true';
	const url = proxyPath('/api/v1/rak/download-latex-pdf', params);
	const res = await fetch(url, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ zspId, contestName: contestName ?? null })
	});
	if (!res.ok) throw new Error(`HTTP ${res.status}`);
	return res.arrayBuffer();
}

export async function getGadInitial(cityId: number): Promise<GadRequest> {
	return apiFetch(apiPath('/api/v1/gad', { cityId: String(cityId) }));
}

export async function runGad(cityId: number, request: GadRequest): Promise<void> {
	await apiFetch(apiPath('/api/v1/gad', { cityId: String(cityId) }), {
		method: 'POST',
		body: JSON.stringify(request)
	});
}

export async function stopGad(cityId: number): Promise<void> {
	await apiFetch(apiPath('/api/v1/gad/stop', { cityId: String(cityId) }), {
		method: 'POST',
		body: JSON.stringify({})
	});
}

export async function getGadStatus(cityId: number): Promise<Progress> {
	return apiFetch(apiPath('/api/v1/gad/status', { cityId: String(cityId) }));
}

export async function getSakInitial(cityId: number): Promise<SakRequest> {
	return apiFetch(apiPath('/api/v1/sak', { cityId: String(cityId) }));
}

export async function runSak(cityId: number, request: SakRequest): Promise<void> {
	await apiFetch(apiPath('/api/v1/sak', { cityId: String(cityId) }), {
		method: 'POST',
		body: JSON.stringify(request)
	});
}

export async function stopSak(cityId: number): Promise<void> {
	await apiFetch(apiPath('/api/v1/sak/stop', { cityId: String(cityId) }), {
		method: 'POST',
		body: JSON.stringify({})
	});
}

export async function getSakStatus(cityId: number): Promise<Progress> {
	return apiFetch(apiPath('/api/v1/sak/status', { cityId: String(cityId) }));
}

export async function runFixer(cityId: number, command: FixSheetCommand): Promise<void> {
	await apiFetch(apiPath('/api/v1/fixer/start', { cityId: String(cityId) }), {
		method: 'POST',
		body: JSON.stringify(command)
	});
}

export async function stopFixer(cityId: number): Promise<void> {
	await apiFetch(apiPath('/api/v1/fixer/stop', { cityId: String(cityId) }), {
		method: 'POST',
		body: JSON.stringify({})
	});
}

export async function getFixerStatus(cityId: number): Promise<Progress> {
	return apiFetch(apiPath('/api/v1/fixer/status', { cityId: String(cityId) }));
}
