import { writable } from "svelte/store";
import type {City} from "$lib/types";

export const city = writable<City>({id: 0, name: ''});
