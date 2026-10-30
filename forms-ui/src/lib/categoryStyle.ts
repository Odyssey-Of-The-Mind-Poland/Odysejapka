export { CATEGORY_ICONS } from './categoryIcons';

export const DEFAULT_CATEGORY_ICON = 'info-circle';

/**
 * Accent colours a category can be tinted with. Keys are what the backend stores and the
 * apps resolve to theme-specific shades; the values here are the light-mode surface of the
 * mobile app (`AccentSurfaceTheme.light`): tint 100 as background, 500 as border, 700 as text,
 * and 400 / 300 as the outline / duotone body of the background pattern.
 */
export const CATEGORY_COLORS = [
	{ key: 'blue', label: 'Niebieski', background: '#E7F3FF', border: '#C4E1FD', foreground: '#228AF2', pattern: '#D0E7FD', patternFill: '#E1F0FE' },
	{ key: 'orange', label: 'Pomarańczowy', background: '#FFF1DE', border: '#FFDCC2', foreground: '#F24D06', pattern: '#FFE1CB', patternFill: '#FFEDD9' },
	{ key: 'purple', label: 'Fioletowy', background: '#EEEAFF', border: '#D7CFFC', foreground: '#6E52F4', pattern: '#DCD5FD', patternFill: '#EAE5FE' },
	{ key: 'green', label: 'Zielony', background: '#ECFADB', border: '#D2F1AC', foreground: '#5B9315', pattern: '#D7F3B5', patternFill: '#E7F8D1' },
	{ key: 'red', label: 'Czerwony', background: '#FFDEDE', border: '#FDC4C4', foreground: '#F42F2F', pattern: '#FDCDCD', patternFill: '#FFDADA' }
] as const;

export type CategoryColorKey = (typeof CATEGORY_COLORS)[number]['key'];

export function categoryColor(key: string | null | undefined) {
	return CATEGORY_COLORS.find((c) => c.key === key) ?? CATEGORY_COLORS[0];
}
