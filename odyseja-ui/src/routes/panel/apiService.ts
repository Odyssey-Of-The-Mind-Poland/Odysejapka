import { toastStore } from '@skeletonlabs/skeleton';
import type { ToastSettings } from '@skeletonlabs/skeleton';
import type { Problem, PerformanceGroup, Timetable, Problems } from './types';
import { env } from '$env/dynamic/public';

export const BASE_URL = env.PUBLIC_BASE_URL || "http://localhost:8081";

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

export async function saveProblems(problems: Problems) {
  let response = await fetch(BASE_URL + "/problem", {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: getBearer(),
    },
    body: JSON.stringify(problems.problems)
  })
  if (!response.ok) {
    showHappyToast('Coś poszło nie tak :c')
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  showHappyToast('Problemy zapisano pomyślnie')
}

function showHappyToast(message: string) {
  showToast(message, 'variant-filled-tertiary')
}

function showSadToast(message: string) {
  showToast(message, 'variant-filled-tertiary')
}

function showToast(message: string, background: string) {
  const t: ToastSettings = {
    message: message,
    timeout: 3000,
    background: background
  };
  toastStore.trigger(t);
}

function getBearer(): string {
  return 'Bearer ' + getCookie("access_token")
}

// @ts-ignore
function getCookie(name: string): string {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) { // @ts-ignore
    return parts.pop().split(';').shift();
  }
}