export enum SpontanType {
	VERBAL = 'VERBAL',
	MANUAL = 'MANUAL',
	VERBAL_MANUAL = 'VERBAL_MANUAL'
}

export function isSpontanType(v: string): v is SpontanType {
	return (Object.values(SpontanType) as String[]).includes(v);
}

export function isManual(type: SpontanType | undefined) {
	return type == 'MANUAL' || type == 'VERBAL_MANUAL';
}

export function getTypeDisplayName(type: SpontanType): string {
	if (type === 'VERBAL_MANUAL') return 'Manualno słowny';
	if (type === 'VERBAL') return 'Słowny';
	if (type === 'MANUAL') return 'Manualny';
	return '';
}
