export type Group = {
  city: string;
  problem: number;
  age: number;
  stage: number;
  part: number;
  league: string;
}

type Performance = {
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