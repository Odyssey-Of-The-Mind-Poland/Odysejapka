export enum SpontantType {
	VERBAL = 'VERBAL',
	MANUAL = 'MANUAL',
	VERBAL_MANUAL = 'VERBAL_MANUAL'
}

export function isManual(type: SpontantType | undefined) {
	return type == 'MANUAL' || type == 'VERBAL_MANUAL';
}

export function getTypeDisplayName(type: SpontantType): string {
	if (type === 'VERBAL_MANUAL') return 'Manualno słowny';
	if (type === 'VERBAL') return 'Słowny';
	if (type === 'MANUAL') return 'Manualny';
	return '';
}
