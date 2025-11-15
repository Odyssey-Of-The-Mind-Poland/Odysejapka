import type {LayoutServerLoad} from "../../.svelte-kit/types/src/routes/$types";

export const load: LayoutServerLoad = async (event) => {
  return {
    session: await event.locals.auth(),
  }
}
