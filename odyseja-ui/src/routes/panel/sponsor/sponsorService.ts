import {get} from "$lib/apiService";
import type {Sponsor, Sponsors} from "$lib/types";

export async function fetchSponsors(): Promise<Sponsors> {
    const data = await get('/sponsor');
    const sponsors = data as Sponsor[][];
    sponsors.push([]);
    return {sponsors: sponsors} as Sponsors;
}