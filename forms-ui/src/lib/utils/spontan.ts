export function isManual(type: String | undefined) {
	return type == 'MANUAL' || type == 'VERBAL_MANUAL';
}

export function getTypeDisplayName(type: 'VERBAL' | 'MANUAL' | 'VERBAL_MANUAL'): string {
	if (type === 'VERBAL_MANUAL') return 'Manualno słowny';
	if (type === 'VERBAL') return 'Słowny';
	if (type === 'MANUAL') return 'Manualny';
	return '';
}
