import type {Performance, PerformanceGroup, Progress, Timetable} from "$lib/types";
import {get, post, del} from "$lib/apiService";

export async function fetchTimeTable(): Promise<Timetable> {
    const timeTable: PerformanceGroup[] = await get('/api/v2/timeTable');
    return {timetable: timeTable} as Timetable;
}

export async function savePerformance(performance: Performance) {
    await post(performance, '/timeTable', 'Dodano przedstawienie');
}

export async function importZsp(zspId: string, cityId: number) {
    await post({zspId: zspId}, `/timeTable/import?cityId=${cityId}`, 'Rozpoczęto import');
}

export async function stopImport() {
    await post({}, '/timeTable/import/stop', 'Zatrzymano generowanie arkuszy');
}

export async function getImportStatus(): Promise<Progress> {
    return await get('/timeTable/import/status');
}

export async function deletePerformance(performanceId: number) {
    del('/timeTable/' + performanceId, 'Występ usunięty pomyślnie');
}