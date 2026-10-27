export { CATEGORY_ICONS } from './categoryIcons';

export const DEFAULT_CATEGORY_ICON = 'info-circle';

/**
 * Accent colours a category can be tinted with. Keys are what the backend stores and
 * the apps resolve to theme-specific shades; hex values are only the preview here
 * (light-mode tint and strong shade from the design system).
 */
export const CATEGORY_COLORS = [
	{ key: 'blue', label: 'Niebieski', background: '#E7F3FF', foreground: '#228AF2' },
	{ key: 'orange', label: 'Pomarańczowy', background: '#FFF1DE', foreground: '#F24D06' },
	{ key: 'purple', label: 'Fioletowy', background: '#EEEAFF', foreground: '#6E52F4' },
	{ key: 'green', label: 'Zielony', background: '#ECFADB', foreground: '#5B9315' },
	{ key: 'red', label: 'Czerwony', background: '#FFDEDE', foreground: '#F42F2F' }
] as const;

export type CategoryColorKey = (typeof CATEGORY_COLORS)[number]['key'];

export function categoryColor(key: string | null | undefined) {
	return CATEGORY_COLORS.find((c) => c.key === key) ?? CATEGORY_COLORS[0];
}
