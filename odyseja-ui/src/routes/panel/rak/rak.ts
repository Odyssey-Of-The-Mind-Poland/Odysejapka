import {BASE_URL, getBearer, showHappyToast, showSadToast} from "$lib/apiService";


export async function generateCsv(zspIdRequest: string) {
    const response = await fetch(BASE_URL + '/api/v1/rak/generate', {
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
    const response = await fetch(BASE_URL + '/api/v1/rak/download-html', {
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

export async function generatePdfResults(zspIdRequest: string) {
    const response = await fetch(BASE_URL + '/api/v1/rak/download-pdf', {
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
    return response.arrayBuffer();
}

export async function generateShortPdfResults(zspIdRequest: string) {
    const response = await fetch(BASE_URL + '/api/v1/rak/download-short-pdf', {
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
    return response.arrayBuffer();
}

export async function generateDetailedCsv(zspId: string): Promise<ArrayBuffer> {
    const response = await fetch('/api/v1/rak/generate-detailed', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({zspId})
    });
    return response.arrayBuffer();
}