import adapter from '@sveltejs/adapter-node';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	preprocess: vitePreprocess({
		script: {
			typescript: true
		}
	}),

	kit: {
		adapter: adapter(),
		alias: {
			$components: "src/components",
			"@/*": "./lib/*",
		},
	}
};

export default config;
