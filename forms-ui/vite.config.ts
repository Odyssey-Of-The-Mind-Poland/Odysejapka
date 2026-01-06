import tailwindcss from '@tailwindcss/vite';
import { sveltekit } from '@sveltejs/kit/vite';
import {defineConfig, loadEnv} from 'vite';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';
import adapter from '@sveltejs/adapter-auto';

export default defineConfig(({mode}) => {
	const env = loadEnv(mode, process.cwd(), '');

	return {
		preprocess: vitePreprocess({ script: true }),
		kit: {
			adapter: adapter()
		},
		plugins: [tailwindcss(), sveltekit()],
		preview: {
			allowedHosts: ['grzybek.snet.ovh']
		},
		server: {
			port: 5172,
			proxy: {
				'/api': {
					target: env.VITE_API_BASE_URL,
					changeOrigin: true,
					secure: false,
					ws: true,
					configure: (proxy, _options) => {
						proxy.on('error', (err, _req, _res) => {
							console.log('proxy error', err);
						});
						proxy.on('proxyReq', (proxyReq, req, _res) => {
							console.log('Sending Request to the Target:', req.method, req.url);
						});
						proxy.on('proxyRes', (proxyRes, req, _res) => {
							console.log('Received Response from the Target:', proxyRes.statusCode, req.url);
						});
					}
				}
			}
		}
	};
});
