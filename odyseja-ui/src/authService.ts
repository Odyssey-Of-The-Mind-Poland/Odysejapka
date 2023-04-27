// @ts-ignore
import auth0 from 'auth0-js';
import { goto } from '$app/navigation';

export const client = new auth0.WebAuth({
  clientID: '8TI8RllRK5wf5l1Rv85msCTOF0e88lZg',
  domain: 'odyseja.eu.auth0.com',
  responseType: 'token id_token',
  audience: 'https://app.odyseja.org',
  redirectUri: 'http://localhost:5173/panel',
  scope: 'openid profile'
});

export async function login() {
  try {
    await client.authorize();
  } catch (e) {
    console.error(e);
  }
}

export async function logout() {
  client.logout();
  await goto('/');
}