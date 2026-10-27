<script lang="ts">
	import CategoryIcon from '$lib/components/CategoryIcon.svelte';
	import { CATEGORY_COLORS, CATEGORY_ICONS, categoryColor } from '$lib/categoryStyle';

	let {
		icon = $bindable(null),
		color = $bindable(null),
		label,
		inheritLabel
	}: {
		icon: string | null;
		color: string | null;
		/** Text shown inside the preview tile. */
		label: string;
		/** When set, an extra "inherit" choice clears the value, so the tile follows this fallback. */
		inheritLabel?: string;
	} = $props();

	let preview = $derived(categoryColor(color));
</script>

<div class="flex flex-col gap-5 py-2">
	<div class="flex items-center gap-3">
		<div
			class="flex h-20 w-44 items-end rounded-2xl border-2 p-4 text-sm font-medium"
			style="background:{preview.background};border-color:{preview.foreground}33;color:{preview.foreground}"
		>
			<span class="truncate">{label}</span>
		</div>
		<div
			class="flex size-12 items-center justify-center rounded-full"
			style="background:{preview.background};color:{preview.foreground}"
		>
			<CategoryIcon name={icon} class="size-6" />
		</div>
	</div>

	<div>
		<p class="text-sm font-medium mb-2">Kolor</p>
		<div class="flex flex-wrap items-center gap-2" role="radiogroup" aria-label="Kolor">
			{#if inheritLabel}
				<button
					type="button"
					role="radio"
					aria-checked={color === null}
					class="h-9 rounded-full border px-3 text-xs hover:bg-muted {color === null ? 'ring-2 ring-offset-2 ring-foreground' : ''}"
					onclick={() => (color = null)}
				>
					{inheritLabel}
				</button>
			{/if}
			{#each CATEGORY_COLORS as option (option.key)}
				<button
					type="button"
					role="radio"
					aria-checked={color === option.key}
					aria-label={option.label}
					title={option.label}
					class="size-9 rounded-full border-2 transition-shadow {color === option.key ? 'ring-2 ring-offset-2 ring-foreground' : ''}"
					style="background:{option.background};border-color:{option.foreground}"
					onclick={() => (color = option.key)}
				></button>
			{/each}
		</div>
	</div>

	<div>
		<p class="text-sm font-medium mb-2">Ikona</p>
		<div class="grid grid-cols-8 gap-1.5 max-h-56 overflow-y-auto pr-1" role="radiogroup" aria-label="Ikona">
			{#if inheritLabel}
				<button
					type="button"
					role="radio"
					aria-checked={icon === null}
					title={inheritLabel}
					class="col-span-2 flex h-10 items-center justify-center rounded-md border px-2 text-xs hover:bg-muted {icon === null ? 'border-foreground bg-muted' : 'border-transparent'}"
					onclick={() => (icon = null)}
				>
					{inheritLabel}
				</button>
			{/if}
			{#each Object.keys(CATEGORY_ICONS) as key (key)}
				<button
					type="button"
					role="radio"
					aria-checked={icon === key}
					aria-label={key}
					title={key}
					class="flex size-10 items-center justify-center rounded-md border hover:bg-muted {icon === key ? 'border-foreground bg-muted' : 'border-transparent'}"
					onclick={() => (icon = key)}
				>
					<CategoryIcon name={key} class="size-5" />
				</button>
			{/each}
		</div>
	</div>
</div>
