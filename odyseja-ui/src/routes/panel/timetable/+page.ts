import type { PageLoad } from './$types';
import type { PerformanceGroup, Timetable } from './types';

export const load = (({params}) => {
  return getTimeTable();
}) satisfies PageLoad;

async function getTimeTable(): Promise<Timetable> {
  const response = await fetch('http://localhost:8081/api/v2/timeTable', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Network response was not ok');
  }

  const timeTable: PerformanceGroup[] = await response.json();
  return {timetable: timeTable} as Timetable;
}

// Use the function
getTimeTable()
    .then(timeTable => {
      console.log(timeTable);
    })
    .catch(error => {
      console.error('There has been a problem with your fetch operation:', error);
    });