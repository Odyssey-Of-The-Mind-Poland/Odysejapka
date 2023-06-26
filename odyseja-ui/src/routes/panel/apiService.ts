import { toastStore } from '@skeletonlabs/skeleton';
import type { ToastSettings } from '@skeletonlabs/skeleton';
import type { Problem, PerformanceGroup, Timetable, Problems, Performance } from './types';
import { env } from '$env/dynamic/public';

const BASE_URL = env.PUBLIC_BASE_URL || "http://localhost:8081";

async function request(method: string, url: string, body?: any): Promise<any> {
  const response = await fetch(BASE_URL + url, {
    method: method,
    headers: {
      'Content-Type': 'application/json',
      Authorization: getBearer(),
    },
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    showToast(`Coś poszło nie tak :c`, 'variant-filled-danger');
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return await response.json();
}

export async function fetchTimeTable(): Promise<Timetable> {
  const timeTable: PerformanceGroup[] = await request('GET', '/api/v2/timeTable');
  return { timetable: timeTable } as Timetable;
}

export async function savePerformance(performance: Performance) {
  await request('PUT', '/timeTable', performance);
  showToast('Występ zapisany pomyślnie', 'variant-filled-tertiary');
}

export async function fetchProblems(): Promise<Problems> {
  const problems: Problem[] = await request('GET', "/problem");
  return { problems };
}

export async function saveProblems(problems: Problems) {
  await request('PUT', "/problem", problems.problems);
  showToast('Problemy zapisano pomyślnie', 'variant-filled-tertiary');
}

function showToast(message: string, background: string) {
  const toastSettings: ToastSettings = {
    message,
    timeout: 3000,
    background
  };
  toastStore.trigger(toastSettings);
}

function getBearer(): string {
  try {
    return 'Bearer ' + getCookie("access_token");
  }
  catch (e) {
    return '';
  }
}

function getCookie(name: string): string {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(';').shift() || '';
  return '';
}
