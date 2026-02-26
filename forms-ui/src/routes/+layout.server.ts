import type {LayoutServerLoad} from "../../.svelte-kit/types/src/routes/$types";

export const load: LayoutServerLoad = async (event) => {
  const session = await event.locals.auth();

  // Strip the accessToken before sending to the browser.
  // The token stays server-side and is only used by the BFF proxy.
  if (session) {
    const { accessToken, ...safeSession } = session as any;
    return { session: safeSession };
  }

  return { session };
}
