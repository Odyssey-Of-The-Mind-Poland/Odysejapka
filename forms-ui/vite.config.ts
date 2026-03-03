import tailwindcss from '@tailwindcss/vite';
import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
	plugins: [tailwindcss(), sveltekit()],
	preview: {
		allowedHosts: ['grzybek.snet.ovh']
	},
	server: {
		port: 5172,
		proxy: {
			'/ws': {
				target: 'http://localhost:8081',
				ws: true,
				changeOrigin: true
			}
		}
	}
});
