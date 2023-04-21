import auth0 from 'auth0-js';

const client = new auth0.WebAuth({
    clientID: '8TI8RllRK5wf5l1Rv85msCTOF0e88lZg',
    domain: 'odyseja.eu.auth0.com',
    responseType: 'token id_token',
    audience: 'https://app.odyseja.org',
    redirectUri: 'http://localhost:5173',
    scope: 'openid profile'
});

export async function login() {
    try {
        await client.authorize();
    } catch (e) {
        console.error(e);
    }
}

export function logout() {
    client.logout();
}

export function handleAuthentication(callback) {
    client.parseHash((error, authResult) => {
        if (authResult && authResult.accessToken && authResult.idToken) {
            callback(authResult.accessToken);
        } else if (error) {
            console.error('Error parsing the authentication result:', error);
        }
    });
}
