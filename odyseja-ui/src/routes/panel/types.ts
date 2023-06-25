
export type Group = {
  city: string;
  problem: number;
  age: number;
  stage: number;
  part: number;
  league: string;
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