import type {ToastSettings} from '@skeletonlabs/skeleton';
import {toastStore} from '@skeletonlabs/skeleton';
import type {Info, InfoCategory, Infos, Performance, PerformanceGroup, Problem, Problems, Timetable} from './types';
import {env} from '$env/dynamic/public';

export const BASE_URL = env.PUBLIC_BASE_URL || "http://localhost:8081";

export async function fetchTimeTable(): Promise<Timetable> {
    const response = await fetch(BASE_URL + '/api/v2/timeTable', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.status}`);
    }

    const timeTable: PerformanceGroup[] = await response.json();
    return {timetable: timeTable} as Timetable;
}

export async function savePerformance(performance: Performance) {
    const response = await fetch(BASE_URL + '/timeTable', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: getBearer(),
        },
        body: JSON.stringify(performance)
    })
    if (!response.ok) {
        showSadToast('Coś poszło nie tak :c')
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    showHappyToast('Występ zapisany pomyślnie')
}

export async function deletePerformance(performanceId: number) {
    const response = await fetch(BASE_URL + '/timeTable/' + performanceId, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            Authorization: getBearer(),
        }
    })
    if (!response.ok) {
        showSadToast('Coś poszło nie tak :c')
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    showHappyToast('Występ usunięty pomyślnie')
}

export async function fetchProblems(): Promise<Problems> {
    let response = await fetch(BASE_URL + "/problem", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    let problems = data as Problem[]
    return {problems: problems};
}

export async function fetchInfo(): Promise<Infos> {
    let response = await fetch(BASE_URL + "/info", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    const categories = await fetchInfoCategory();
    return {infos: data as Info[], categories: categories} as Infos;
}

export async function fetchInfoCategory(): Promise<InfoCategory[]> {
    let response = await fetch(BASE_URL + "/info/category", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data as InfoCategory[];
}

export async function saveProblems(problems: Problems) {
    let response = await fetch(BASE_URL + "/problem", {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            Authorization: getBearer(),
        },
        body: JSON.stringify(problems.problems)
    })
    if (!response.ok) {
        showSadToast('Coś poszło nie tak :c')
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    showHappyToast('Problemy zapisano pomyślnie')
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