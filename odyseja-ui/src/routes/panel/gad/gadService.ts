import {type GadProgress, type GadRequest} from "$lib/types";
import {get, post} from "$lib/apiService";

export async function runGad(gadRequest: GadRequest) {
    await post(gadRequest, '/api/v1/gad', 'Rozpoczeto generowanie arkuszy');
}

export async function stopGadRun() {
    await post({}, '/api/v1/gad/stop', 'Zatrzymano generowanie arkuszy');
}

export async function getGadStatus(): Promise<GadProgress> {
    return await get('/api/v1/gad/status');
}