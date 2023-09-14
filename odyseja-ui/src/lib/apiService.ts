import type {ToastSettings} from '@skeletonlabs/skeleton';
import {toastStore} from '@skeletonlabs/skeleton';
import {env} from '$env/dynamic/public';

export const BASE_URL = env.PUBLIC_BASE_URL || "http://localhost:8081";


export async function get(path: string): Promise<any> {
    const response = await fetch(BASE_URL + path, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.status}`);
    }

    return  await response.json();
}

export async function post(request: any, path: string, succesText: string): Promise<any> {
    const response = await fetch(BASE_URL + path, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: getBearer(),
        },
        body: JSON.stringify(request)
    })
    if (!response.ok) {
        showSadToast('Coś poszło nie tak :c')
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    showHappyToast(succesText === undefined ? 'Akcaj wykonana pomyślnie' : succesText)
}

export async function put(request: any, path: string, succesText: string): Promise<any> {
    const response = await fetch(BASE_URL + path, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            Authorization: getBearer(),
        },
        body: JSON.stringify(request)
    })
    if (!response.ok) {
        showSadToast('Coś poszło nie tak :c')
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    showHappyToast(succesText === undefined ? 'Akcaj wykonana pomyślnie' : succesText)
}


export async function del(path: string, succesText: string): Promise<any> {
    const response = await fetch(BASE_URL + path, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            Authorization: getBearer(),
        },
    })
    if (!response.ok) {
        showSadToast('Coś poszło nie tak :c')
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    showHappyToast(succesText === undefined ? 'Usunięto pomyślnie' : succesText)
}

function showHappyToast(message: string) {
    showToast(message, 'variant-filled-tertiary')
}

function showSadToast(message: string) {
    showToast(message, 'variant-filled-tertiary')
}

function showToast(message: string, background: string) {
    const t: ToastSettings = {
        message: message,
        timeout: 3000,
        background: background
    };
    toastStore.trigger(t);
}

function getBearer(): string {
    return 'Bearer ' + getCookie("access_token")
}

// @ts-ignore
function getCookie(name: string): string {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) { // @ts-ignore
        return parts.pop().split(';').shift();
    }
}