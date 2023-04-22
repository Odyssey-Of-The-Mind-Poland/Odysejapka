import { client } from './authService.js';

const publicRoutes = ['/'];

export async function checkAuth(location) {
    const currentLocation = location.route.id;
    console.log('Checking auth for location:', currentLocation);

    if (publicRoutes.includes(currentLocation)) {
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