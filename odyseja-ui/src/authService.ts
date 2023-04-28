// @ts-ignore
import auth0 from 'auth0-js';
import { goto } from '$app/navigation';

export const client = new auth0.WebAuth({
  clientID: '8TI8RllRK5wf5l1Rv85msCTOF0e88lZg',
  domain: 'odyseja.eu.auth0.com',
  responseType: 'token id_token',
  audience: 'https://app.odyseja.org',
  redirectUri: 'http://localhost:5173/callback',
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
  setCookie('id_token', '')
  await goto('/');
}

export function handleAuthentication() {
  return new Promise((resolve, reject) => {
    // @ts-ignore
    client.parseHash((error, authResult) => {
      if (authResult && authResult.accessToken && authResult.idToken) {
        // The authentication was successful, and you have the access token and ID token
        console.log('Access Token:', authResult.accessToken);
        console.log('ID Token:', authResult.idToken);

        // Save the ID token as a cookie
        setCookie('id_token', authResult.idToken);

        resolve(authResult);
      } else if (error) {
        console.error('Error parsing the authentication result:', error);
        reject(error);
      }
    });
  });
}


function setCookie(name: string, value: string) {
  document.cookie = name + "=" + value;
}