<script lang="ts">
	import AccentPattern from '$lib/components/AccentPattern.svelte';
	import { categoryColor } from '$lib/categoryStyle';
	import { cn } from '$lib/utils.js';

	let {
		label,
		icon,
		color,
		class: className = ''
	}: {
		label: string;
		icon: string | null | undefined;
		color: string | null | undefined;
		class?: string;
	} = $props();

	let surface = $derived(categoryColor(color));
</script>

<!-- Mirrors the mobile `InfoTile`: tinted surface, 2px border, 16px radius, pattern behind a bottom-left title. -->
<div
	class={cn('relative flex items-end overflow-hidden rounded-2xl border-2 p-3.5 text-sm font-semibold', className)}
	style="background:{surface.background};border-color:{surface.border};color:{surface.foreground}"
>
	<AccentPattern {icon} stroke={surface.pattern} fill={surface.patternFill} />
	<span class="relative line-clamp-2 text-left">{label}</span>
</div>
