import { writable } from 'svelte/store';

const tokenStore = writable(null);

export default tokenStore;
