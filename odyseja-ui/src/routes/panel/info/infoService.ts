import type {Info, InfoCategory, Infos} from "$lib/types";
import {get, del, post} from "$lib/apiService";

export async function fetchInfo(): Promise<Infos> {
    const data = await get('/info');
    const categories = await fetchInfoCategory();
    return {infos: data as Info[], categories: categories} as Infos;
}

export async function saveInfo(info: Info): Promise<Info> {
    return post(info, '/info', "Informacja zapisana pomyślnie");
}

async function fetchInfoCategory(): Promise<InfoCategory[]> {
    const data = await get('/info/category');
    return data as InfoCategory[];
}

export async function deleteInfo(infoId: number): Promise<InfoCategory[]> {
    const data = await del(`/info/${infoId}`, "Info usunięte pomyślnie");
    return data as InfoCategory[];
}