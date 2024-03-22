import {post} from "$lib/apiService";
import type {ZspIdRequest} from "$lib/types";


export async function generateCsv(zspIdRequest: ZspIdRequest) {
    await post(zspIdRequest, '/api/v1/tm/generate', '');
}