import type {PageLoad} from './$types';
import {fetchSponsors} from "./sponsorService";

export const load = (({params}) => {
    return fetchSponsors();
}) satisfies PageLoad;


