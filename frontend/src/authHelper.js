import { client } from './authService.js';

const publicRoutes = ['/'];

export async function checkAuth(location) {
    console.log('Checking auth for location:', location);

    if (publicRoutes.includes(location.pathname)) {
        return { authenticated: true };
    }

    return new Promise((resolve) => {
        client.checkSession({}, (error, authResult) => {
            if (authResult && authResult.accessToken) {
                resolve({ authenticated: true, accessToken: authResult.accessToken });
            } else {
                resolve({ authenticated: false });
            }
        });
    });
}