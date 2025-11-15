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
        async jwt({token, account}) {
            if (account) {
                token.accessToken = account.access_token;
                token.idToken = account.id_token;
            }
            return token;
        },
        async session({session, token}) {
            session.accessToken = token.accessToken as string;
            session.idToken = token.idToken as string;
            return session;
        },
    },
});
