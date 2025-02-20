export type Group = {
    city: string;
    problem: number;
    age: number;
    stage: number;
    part: number;
    league: string;
}

export function getGroupTitle(group: Group): string {
    let name = `Scena: ${group.stage} • Problem ${group.problem} • Gr. wiekowa ${group.age}`;
    if (group.part) {
        name = `${name} • Część ${group.part}`;
    }

    if (group.league) {
        name = `${name} • Liga ${group.league}`;
    }
    return name;
}

export function compareGroups(a: Group, b: Group): number {
    if (a.problem < b.problem) return -1;
    if (a.problem > b.problem) return 1;

    if (a.age < b.age) return -1;
    if (a.age > b.age) return 1;

    if (a.stage < b.stage) return -1;
    if (a.stage > b.stage) return 1;

    if (a.part < b.part) return -1;
    if (a.part > b.part) return 1;

    if (a.league < b.league) return -1;
    if (a.league > b.league) return 1;

    return 0;
}

export type Performance = {
    id: number;
    city: string;
    team: string;
    problem: number;
    age: number;
    stage: number;
    performance: string;
    spontan: string;
    part: number;
    performanceDay: string;
    spontanDay: string;
    league: string;
}

export function comparePerformances(a: Performance, b: Performance): number {
    if (a.id < b.id) return -1;
    if (a.id > b.id) return 1;

    return 0;
}


export type PerformanceGroup = {
    group: Group;
    performances: Performance[];
}

export type Timetable = {
    timetable: PerformanceGroup[];
}

export type Problems = {
    problems: Problem[]
}

export type Problem = {
    id: number,
    name: string
}

export type Cities = {
    cities: City[]
}

export type City = {
    id: number,
    name: string
}

export type Infos = {
    infos: Info[],
    categories: InfoCategory[]
}

export type Info = {
    id: number,
    infoName: string,
    infoText: string,
    city: number,
    category: number,
    sortNumber: number,
    categoryName: string
}

export type InfoCategory = {
    id: number,
    name: string,
}

export type Stages = {
    stages: Stage[]
}

export type Stage = {
    id: number,
    number: number,
    city: number,
    name: string
}

export type Sponsors = {
    sponsors: Sponsor[][]
}

export type Sponsor = {
    id: number,
    rowIndex: number,
    columnIndex: number,
}

export type BreakingChange = {
    version: string
}

export type GadRequest = {
    templatesFolderId: string,
    destinationFolderId: string,
    zspId: string,
    problemPunctuationCells: Map<number, PunctationCells>
}

export type PunctationCells = {
    dt: string,
    style: string,
    penalty: string,
    balsa: string;
    anomaly: string;
}

export type SakRequest = {
    templatesFolderId: string,
    zspId: string,
}

export type Progress = {
    status: Status,
    progress: number,
}

export enum Status {
    STOPPED = "STOPPED",
    RUNNING = "RUNNING",
}

export type ZspIdRequest = {
    zspId: String
}