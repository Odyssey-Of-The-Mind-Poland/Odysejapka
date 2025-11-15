import type {Actions} from "@sveltejs/kit";
import {signOut} from "../../auth";

export const actions = { default: signOut } satisfies Actions
