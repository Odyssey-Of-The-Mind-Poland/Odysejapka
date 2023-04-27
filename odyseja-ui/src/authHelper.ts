import type { Page } from '@sveltejs/kit';
import { client } from './authService';

const publicRoutes = ['/'];

export async function checkAuth(location: Page) {
    const currentLocation = location.route.id;
    console.log('Checking auth for location:', currentLocation);

    if (publicRoutes.includes(<string> currentLocation)) {
        return { authenticated: true };
    }

    return new Promise((resolve) => {
        // @ts-ignore
      client.checkSession({}, (error, authResult) => {
            if (authResult && authResult.accessToken) {
                resolve({ authenticated: true, accessToken: authResult.accessToken });
            } else {
                resolve({ authenticated: false });
            }
        });
    });
}