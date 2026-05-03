import type {Performance, PerformanceGroup, Progress, Timetable} from "$lib/types";
import {get, post, del} from "$lib/apiService";

export async function fetchTimeTable(): Promise<Timetable> {
    const timeTable: PerformanceGroup[] = await get('/timeTable/groups');
    return {timetable: timeTable} as Timetable;
}

export async function savePerformance(performance: Performance) {
    await post(performance, '/timeTable', 'Dodano przedstawienie');
}

export async function importZsp(zspId: string, cityId: number) {
    await post({zspId: zspId}, '/timeTable/import/zsp', 'Rozpoczęto import');
}

export async function stopImport() {
    await post({}, '/timeTable/import/zsp/stop', 'Zatrzymano generowanie arkuszy');
}

export async function getImportStatus(): Promise<Progress> {
    return await get('/timeTable/import/zsp/status');
}

export async function deletePerformance(performanceId: number) {
    del('/timeTable/' + performanceId, 'Występ usunięty pomyślnie');
}