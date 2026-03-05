import {writable} from 'svelte/store';
import {browser} from '$app/environment';

export type City = {
    id: number;
    name: string;
};

const STORAGE_KEY = 'selectedCity';

function loadCity(): City | null {
    if (!browser) return null;
    try {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (!raw) return null;
        return JSON.parse(raw) as City;
    } catch {
        return null;
    }
}

function persistCity(city: City | null) {
    if (!browser) return;
    if (city) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(city));
    } else {
        localStorage.removeItem(STORAGE_KEY);
    }
}

export const selectedCity = writable<City | null>(loadCity());

selectedCity.subscribe(persistCity);

export function setCity(city: City) {
    selectedCity.set(city);
}

export function clearCity() {
    selectedCity.set(null);
}
