import type { Handle } from '@sveltejs/kit';
import { redirect } from '@sveltejs/kit';
import jwt_decode from 'jwt-decode';

const publicRoutes = ['/', '/callback'];

export const handle: Handle = async ({event, resolve}) => {
  let token = event.cookies.get('id_token');

  if (!token || token === '' || isTokenExpired(token)) {
    if (!(publicRoutes.includes(event.url.pathname))) {
      throw redirect(303, '/');
    }
  }

  return resolve(event);
};

function isTokenExpired(token: string | undefined) {
  if (!token) {
    return true;
  }

  try {
    const decoded = jwt_decode(token);
    const currentTime = Date.now() / 1000;

    // @ts-ignore
    if (decoded.exp < currentTime) {
      return true;
    }
  } catch (error) {
    console.error('Error decoding token:', error);
  }

  return false;
}