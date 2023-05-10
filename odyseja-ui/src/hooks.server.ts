import type { Handle } from '@sveltejs/kit';
import { redirect } from '@sveltejs/kit';
import jwt_decode from 'jwt-decode';

const routes = [
  { path: '/', roles: [] },
  { path: '/callback', roles: [] },
  { path: '/panel/*', roles: ['ADMIN'] },
];

export const handle: Handle = async ({event, resolve}) => {
  let token = event.cookies.get('access_token');

  const route = routes.find(route => matchPath(event.url.pathname, route.path));

  if (!route) {
    return resolve(event);
  }

  if (!token || token === '' || isTokenExpired(token)) {
    if (route.roles.length > 0) {
      throw redirect(303, '/');
    }
  } else {
    if (route.roles.length > 0) {
      const userRoles = getUserRoles(token);

      if (!route.roles.some(role => userRoles.includes(role))) {
        throw redirect(303, '/');
      }
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

function getUserRoles(token: string) {
  try {
    const decoded = jwt_decode(token);

    // @ts-ignore
    return decoded['https://app.odyseja.org/roles'] || [];
  } catch (error) {
    console.error('Error decoding token:', error);
    return [];
  }
}

function matchPath(url: string, path: string) {

  if (path.endsWith('/*')) {
    return url.startsWith(path.slice(0, -2));
  }

  return url === path;
}