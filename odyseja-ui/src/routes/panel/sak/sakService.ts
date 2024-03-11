import type {Progress, SakRequest} from "$lib/types";
import {get, post} from "$lib/apiService";

export async function runSak(sakRequest: SakRequest) {
    await post(sakRequest, '/api/v1/sak', 'Rozpoczeto generowanie arkuszy');
}

export async function stopSakRun() {
    await post({}, '/api/v1/sak/stop', 'Zatrzymano generowanie arkuszy');
}

export async function getSakStatus(): Promise<Progress> {
    return await get('/api/v1/sak/status');
}