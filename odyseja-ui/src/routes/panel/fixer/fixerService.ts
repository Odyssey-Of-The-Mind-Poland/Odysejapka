import {post} from "$lib/apiService";

export async function runFixer(fixSheetCommand: FixSheetCommand) {
    await post(fixSheetCommand, '/api/v1/fixer/start', 'Rozpoczeto naprawianie arkuszy');
}

export async function stopFixer() {
    await post({}, '/api/v1/fixer/stop', 'Zatrzymano naprawianie arkuszy');
}

export type FixSheetCommand = {
    folderId: string,
    pattern: string,
    cells: ReplacementCell[]
}

export type ReplacementCell = {
    sheetName: string,
    cell: string,
    value: string,
}