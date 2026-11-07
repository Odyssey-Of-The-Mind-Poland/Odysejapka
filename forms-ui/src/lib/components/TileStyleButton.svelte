<script lang="ts">
	import * as Dialog from '$lib/components/ui/dialog/index.js';
	import { Button } from '$lib/components/ui/button/index.js';
	import CategoryStylePicker from '$lib/components/CategoryStylePicker.svelte';
	import InfoTilePreview from '$lib/components/InfoTilePreview.svelte';
	import { CATEGORY_COLORS } from '$lib/categoryStyle';
	import { cn } from '$lib/utils.js';

	let {
		icon = $bindable(null),
		color = $bindable(null),
		label,
		defaultIcon,
		defaultColorIndex,
		disabled = false,
		class: className = '',
		onSave
	}: {
		icon: string | null;
		color: string | null;
		/** Text on the tile, e.g. the name of the stage. */
		label: string;
		/** Icon of the tile while it has none of its own (what the app falls back to). */
		defaultIcon: string;
		/** The app colours tiles without a colour by their number, round robin over the palette. */
		defaultColorIndex: number;
		/** Show the tile only, without the picker (outside of edit mode). */
		disabled?: boolean;
		class?: string;
		onSave?: () => void;
	} = $props();

	let open = $state(false);

	let shownIcon = $derived(icon ?? defaultIcon);
	let shownColor = $derived(
		color ?? CATEGORY_COLORS[Math.abs(defaultColorIndex) % CATEGORY_COLORS.length].key
	);

	function save() {
		open = false;
		if (onSave == undefined) return;
		onSave();
	}
</script>

{#if disabled}
	<InfoTilePreview
		class={cn('h-10 w-36 shrink-0 rounded-xl border px-2.5 py-1.5 text-xs', className)}
		{label}
		icon={shownIcon}
		color={shownColor}
	/>
{:else}
	<button
		type="button"
		class="focus-visible:ring-ring rounded-xl outline-none focus-visible:ring-2"
		aria-label="Wygląd kafelka: {label}"
		title="Zmień wygląd kafelka"
		onclick={() => (open = true)}
	>
		<InfoTilePreview
			class={cn('h-10 w-36 shrink-0 rounded-xl border px-2.5 py-1.5 text-xs', className)}
			{label}
			icon={shownIcon}
			color={shownColor}
		/>
	</button>

	<Dialog.Root bind:open>
		<Dialog.Content class="max-h-[90vh] overflow-y-auto sm:max-w-xl">
			<Dialog.Header>
				<Dialog.Title>Wygląd kafelka</Dialog.Title>
				<Dialog.Description>{label}</Dialog.Description>
			</Dialog.Header>

			<CategoryStylePicker
				bind:icon
				bind:color
				{label}
				inheritLabel="Domyślny"
				inheritIconLabel="Domyślna"
				showPreview={false}
			/>
			<InfoTilePreview class="h-20 w-44" {label} icon={shownIcon} color={shownColor} />

			<Dialog.Footer>
				<Dialog.Close>
					{#snippet child({ props })}
						<Button {...props} onclick={save}>Gotowe</Button>
					{/snippet}
				</Dialog.Close>
			</Dialog.Footer>
		</Dialog.Content>
	</Dialog.Root>
{/if}
