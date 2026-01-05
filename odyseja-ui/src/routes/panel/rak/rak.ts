import {BASE_URL, getBearer, showHappyToast, showSadToast} from "$lib/apiService";
import {city} from "$lib/cityStore";
import type {City} from "$lib/types";

let currentCity: City = {id: 0, name: ''};
city.subscribe((c) => currentCity = c);

export async function generatePdfResults(zspIdRequest: string) {
    const response = await fetch(BASE_URL + '/api/v1/rak/download-pdf?cityId=' + currentCity.id, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: getBearer(),
        },
        body: JSON.stringify({zspId: zspIdRequest})
    })
    if (!response.ok) {
        showSadToast('Coś poszło nie tak :c')
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    showHappyToast('Pdf generated')
    return response.arrayBuffer();
}

export async function generateShortPdfResults(zspIdRequest: string) {
    const response = await fetch(BASE_URL + '/api/v1/rak/download-short-pdf?cityId=' + currentCity.id, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: getBearer(),
        },
        body: JSON.stringify({zspId: zspIdRequest})
    })
    if (!response.ok) {
        showSadToast('Coś poszło nie tak :c')
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    showHappyToast('P{df generated')
    return response.arrayBuffer();
}