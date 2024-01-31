import type {Handle} from '@sveltejs/kit';
import {redirect} from '@sveltejs/kit';
import Token from './token';

const routes = [
    {path: '/', roles: []},
    {path: '/callback', roles: []},
    {path: '/panel/*', roles: ['ADMIN']},
];

export const handle: Handle = async ({event, resolve}) => {
    const token = new Token(event.cookies.get('access_token'));

    const route = routes.find(route => matchPath(event.url.pathname, route.path));
    console.log('route', route);

    if (!route) {
        return resolve(event);
    }

    if (token.isExpired()) {
        if (route.roles.length > 0) {
            throw redirect(303, '/');
        }
    } else {
        if (route.roles.length > 0) {
            const userRoles = token.getUserRoles();
            console.log('userRoles', userRoles)

            if (!route.roles.some(role => userRoles.includes(role))) {
                throw redirect(303, '/');
            }
        }
    }

    return resolve(event);
};

function matchPath(url: string, path: string) {

    if (path.endsWith('/*')) {
        return url.startsWith(path.slice(0, -2));
    }

    return url === path;
}