import {BASE_URL, getBearer, showHappyToast, showSadToast} from "$lib/apiService";


export async function generateCsv(zspIdRequest: string) {
    const response = await fetch(BASE_URL + '/api/v1/tm/generate', {
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

    showHappyToast('Csv generated')
    return response.text();
}

export async function generateHtmlResults(zspIdRequest: string) {
    const response = await fetch(BASE_URL + '/api/v1/tm/download-html', {
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

    showHappyToast('Csv generated')
    return response.text();
}