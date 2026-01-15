// eslint.config.mjs / eslint.config.js
import prettier from 'eslint-config-prettier';
import { includeIgnoreFile } from '@eslint/compat';
import js from '@eslint/js';
import svelte from 'eslint-plugin-svelte';
import globals from 'globals';
import { fileURLToPath } from 'node:url';
import ts from 'typescript-eslint';
import svelteConfig from './svelte.config.js';
import svelteParser from 'svelte-eslint-parser';

const gitignorePath = fileURLToPath(new URL('./.gitignore', import.meta.url));
const rootDir = fileURLToPath(new URL('.', import.meta.url));

export default ts.config(
	includeIgnoreFile(gitignorePath),

	{
		files: ['**/*.{js,cjs,mjs,ts,cts,mts}'],
		...js.configs.recommended,
		...ts.configs.recommended,
		languageOptions: {
			parser: ts.parser,
			parserOptions: {
				projectService: true,
				project: ['./tsconfig.json'],
				tsconfigRootDir: rootDir
			},
			globals: { ...globals.browser, ...globals.node }
		},
		rules: {
			'no-undef': 'off'
		}
	},

	{
		files: ['**/*.svelte'],
		languageOptions: {
			parser: svelteParser,
			parserOptions: {
				parser: ts.parser,
				project: ['./tsconfig.json'],
				tsconfigRootDir: rootDir,
				extraFileExtensions: ['.svelte']
			}
		},
		...svelte.configs['flat/recommended']
	},

	prettier,
	...svelte.configs.prettier
);
