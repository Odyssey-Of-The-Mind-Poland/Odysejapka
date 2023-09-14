import type { PageLoad } from './$types';
import type {Problem, Problems} from "$lib/types";
import {get} from "$lib/apiService";

export const load = (({params}) => {
  return fetchProblems();
}) satisfies PageLoad;

async function fetchProblems(): Promise<Problems> {
  const data = await get('/problem');
  const problems = data as Problem[]
  return {problems: problems};
}

