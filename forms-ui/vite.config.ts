import tailwindcss from '@tailwindcss/vite';
import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';
import adapter from '@sveltejs/adapter-auto';

export default defineConfig({
	preprocess: vitePreprocess({ script: true }),
	kit: {
		adapter: adapter()
	},
	plugins: [tailwindcss(), sveltekit()],
	preview: {
		allowedHosts: ['grzybek.snet.ovh']
	},
	server: {
		port: 5172
	}
});
