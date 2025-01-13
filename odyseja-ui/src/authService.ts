// @ts-ignore
import auth0 from 'auth0-js';
import {goto} from '$app/navigation';
import {env} from '$env/dynamic/public';
import Token from "./token";

export const FRONTEND_URL = env.PUBLIC_FRONTEND_URL || "http://localhost:5173";
const CLIENT_ID = '8TI8RllRK5wf5l1Rv85msCTOF0e88lZg';

export const client = new auth0.WebAuth({
  clientID: CLIENT_ID,
  domain: 'odyseja.eu.auth0.com',
  responseType: 'token id_token',
  audience: 'https://app.odyseja.org',
  redirectUri: FRONTEND_URL + '/callback',
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
  client.logout({
    returnTo: FRONTEND_URL,
    clientID: '8TI8RllRK5wf5l1Rv85msCTOF0e88lZg'
  });
  setCookie('access_token', '');
  await goto('/');
}

export function handleAuthentication() {
  return new Promise((resolve, reject) => {
    // @ts-ignore
    client.parseHash((error, authResult) => {
      if (authResult && authResult.accessToken && authResult.idToken) {
        console.log('Access Token:', authResult.accessToken);
        console.log('ID Token:', authResult.idToken);
        let token = new Token(authResult.accessToken);

        if (!token.getUserRoles().includes('ADMIN')) {
          client.logout();
          reject('User is not an admin');
        }

        setCookie('access_token', authResult.accessToken);

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