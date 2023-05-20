import type { Problem, PerformanceGroup, Timetable, Problems } from './types';

const BASE_URL = "http://localhost:8081"

export async function fetchTimeTable(): Promise<Timetable> {
  const response = await fetch(BASE_URL + '/api/v2/timeTable', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Network response was not ok: ${response.status}`);
  }

  const timeTable: PerformanceGroup[] = await response.json();
  return {timetable: timeTable} as Timetable;
}

export async function fetchProblems(): Promise<Problems> {
  let response = await fetch(BASE_URL + "/problem", {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  const data = await response.json();
  let problems = data as Problem[]
  return { problems: problems };
}