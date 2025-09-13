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

export async function getPdfTemplate(): Promise<string> {
    const res = await fetch(BASE_URL + '/api/v1/rak/pdf-template', {
        method: 'GET',
        headers: {'Accept': 'text/plain'}
    });
    if (!res.ok) throw new Error('Failed to load template');
    return res.text();
}

export async function savePdfTemplate(body: string): Promise<string> {
    const res = await fetch(BASE_URL + '/api/v1/rak/pdf-template', {
        method: 'POST',
        headers: {'Content-Type': 'text/plain; charset=utf-8'},
        body
    });
    if (!res.ok) throw new Error('Failed to save template');
    return res.text();
}
