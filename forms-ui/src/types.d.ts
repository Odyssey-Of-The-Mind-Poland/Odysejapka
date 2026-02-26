// src/types.d.ts
import "@auth/sveltekit";

declare module "@auth/sveltekit" {
    interface Session {
        // accessToken is kept in the server-side session for the BFF proxy
        // but stripped before being sent to the browser
        accessToken?: string;
    }
}
