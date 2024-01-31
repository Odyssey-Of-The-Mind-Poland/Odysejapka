import type {GadRequest} from "$lib/types";
import {post} from "$lib/apiService";

export async function runGad(gadRequest: GadRequest) {
    await post(gadRequest, '/api/v1/gad', 'Rozpoczeto generowanie arkuszy');
}
