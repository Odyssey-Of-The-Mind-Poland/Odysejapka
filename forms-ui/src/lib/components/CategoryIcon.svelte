<script lang="ts">
	import { CATEGORY_ICONS, DEFAULT_CATEGORY_ICON } from '$lib/categoryStyle';

	let {
		name,
		class: className = 'size-5'
	}: {
		name: string | null | undefined;
		class?: string;
	} = $props();

	// The markup comes from the static CATEGORY_ICONS table, never from user input.
	let markup = $derived(CATEGORY_ICONS[name ?? ''] ?? CATEGORY_ICONS[DEFAULT_CATEGORY_ICON]);
</script>

<svg
	viewBox="0 0 24 24"
	fill="none"
	stroke="currentColor"
	stroke-width="2"
	stroke-linecap="round"
	stroke-linejoin="round"
	class={className}
	aria-hidden="true"
>
	{@html markup}
</svg>
