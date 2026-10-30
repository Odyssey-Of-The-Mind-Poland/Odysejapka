<script lang="ts">
	import { CATEGORY_ICONS, DEFAULT_CATEGORY_ICON } from '$lib/categoryStyle';

	let {
		icon,
		stroke,
		fill,
		cellSize = 28,
		spacing = 28
	}: {
		icon: string | null | undefined;
		/** Outline colour of the glyphs. */
		stroke: string;
		/** Duotone body of the glyphs. */
		fill: string;
		cellSize?: number;
		spacing?: number;
	} = $props();

	// Same geometry as the mobile `AccentPattern`: a brick grid of one glyph, rotated by 16 degrees
	// around the centre of the tile. The design's glyphs are ~25px boxes in a 28px cell.
	const ROTATION = 16;
	const GLYPH_SCALE = 0.9;
	const VIEW_BOX = 24;

	let width = $state(0);
	let height = $state(0);

	// The markup comes from the static CATEGORY_ICONS table, never from user input.
	let markup = $derived(CATEGORY_ICONS[icon ?? ''] ?? CATEGORY_ICONS[DEFAULT_CATEGORY_ICON]);
	let glyph = $derived(cellSize * GLYPH_SCALE);
	let scale = $derived(glyph / VIEW_BOX);

	/** Two brick rows in one repeating tile: even rows are shifted by half a cell. */
	let half = $derived(spacing / 2);
	const patternId = `accent-pattern-${Math.random().toString(36).slice(2, 9)}`;

	function at(x: number, y: number) {
		return `translate(${x - glyph / 2} ${y - glyph / 2}) scale(${scale})`;
	}
</script>

<div class="pointer-events-none absolute inset-0 overflow-hidden" aria-hidden="true" bind:clientWidth={width} bind:clientHeight={height}>
	{#if width > 0 && height > 0}
		<svg {width} {height} viewBox="0 0 {width} {height}">
			<defs>
				<pattern
					id={patternId}
					x="0"
					y={-half}
					width={spacing}
					height={spacing * 2}
					patternUnits="userSpaceOnUse"
					patternTransform="translate({width / 2} {height / 2}) rotate({ROTATION})"
				>
					<g {fill} {stroke} stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
						<g transform={at(half, 0)}>{@html markup}</g>
						<g transform={at(0, spacing)}>{@html markup}</g>
						<g transform={at(spacing, spacing)}>{@html markup}</g>
					</g>
				</pattern>
			</defs>
			<rect {width} {height} fill="url(#{patternId})" />
		</svg>
	{/if}
</div>
