// src/types.d.ts
import "@auth/sveltekit";

declare module "@auth/sveltekit" {
    interface Session {
        accessToken?: string;
        idToken?: string;
    }
}
