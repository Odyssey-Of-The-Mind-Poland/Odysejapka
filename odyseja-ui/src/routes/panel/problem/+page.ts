import { fetchProblems } from '$lib/apiService';
import type { PageLoad } from './$types';

export const load = (({params}) => {
  return fetchProblems();
}) satisfies PageLoad;
