import type {Performance ,PerformanceGroup, Timetable} from "$lib/types";
import {get, post, del} from "$lib/apiService";

export async function fetchTimeTable(): Promise<Timetable> {
    const timeTable: PerformanceGroup[] = await get('/api/v2/timeTable');
    return {timetable: timeTable} as Timetable;
}

export async function savePerformance(performance: Performance) {
    await post(performance, '/timeTable', 'Dodano przedstawienie');
}

export async function deletePerformance(performanceId: number) {
    del('/timeTable/' + performanceId, 'Występ usunięty pomyślnie');
}