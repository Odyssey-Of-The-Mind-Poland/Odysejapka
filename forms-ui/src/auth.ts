import {SvelteKitAuth} from "@auth/sveltekit";
import Auth0 from "@auth/sveltekit/providers/auth0";
import {env} from "$env/dynamic/private";

export const {handle, signIn, signOut} = SvelteKitAuth({
    trustHost: true,
    secret: env.AUTH_SECRET,
    providers: [
        Auth0({
            clientId: env.AUTH0_CLIENT_ID,
            clientSecret: env.AUTH0_CLIENT_SECRET,
            issuer: env.AUTH0_ISSUER,
            authorization: {
                params: {
                    audience: env.AUTH0_AUDIENCE,
                    scope: "openid profile email offline_access",
                },
            },
        }),
    ],
    callbacks: {
        async redirect({url, baseUrl}) {
            // After sign-in, redirect to dashboard
            if (url.startsWith(baseUrl)) return url;
            return `${baseUrl}/dashboard`;
        },
        async jwt({token, account}) {
            if (account) {
                // Store tokens in the server-side JWT — never sent to browser
                token.accessToken = account.access_token;
                token.idToken = account.id_token;
            }
            return token;
        },
        async session({session, token}) {
            // Keep accessToken in session so the BFF proxy can read it
            // via event.locals.auth() on the server side.
            // The root layout strips it before sending to the browser.
            (session as any).accessToken = token.accessToken as string;
            return session;
        },
    },
});
