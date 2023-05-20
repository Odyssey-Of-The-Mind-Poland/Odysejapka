import { fetchTimeTable } from '../apiService';
import type { PageLoad } from './$types';

export const load = (({params}) => {
  return fetchTimeTable();
}) satisfies PageLoad;