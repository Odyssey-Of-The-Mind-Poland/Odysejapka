import { fetchTimeTable } from '$lib/apiService';
import type { PageLoad } from './$types';
import {compareGroups, comparePerformances} from "$lib/types";
import type {Timetable} from "$lib/types";

export const load = (async ({params}) => {
  return initialSort(await fetchTimeTable());
}) satisfies PageLoad;

function initialSort(timeTable: Timetable): Timetable {
  timeTable.timetable.sort((a, b) => compareGroups(a.group, b.group));
  timeTable.timetable.forEach(performanceGroup => {
    performanceGroup.performances.sort((a, b) => comparePerformances(a, b));
  });
  return timeTable;
}