import type { FormEntryType } from './types';


export function recalculateSortIndexes(entries: FormEntryType[]): FormEntryType[] {
	return entries.map((entry, index) => ({
		...entry,
		sortIndex: index,
		entries: recalculateSortIndexes(entry.entries || [])
	}));
}


export function formatSortIndex(
	entry: FormEntryType,
	parentIndex?: string
): string {
	const baseIndex = entry.sortIndex + 1; // Convert 0-based to 1-based for display

	if (parentIndex === undefined) {
		// Top-level entry
		return baseIndex.toString();
	} else {
		// Nested entry - convert number to letter or roman numeral
		const suffix = numberToLetterOrRoman(baseIndex);
		return `${parentIndex}.${suffix}`;
	}
}


function numberToLetterOrRoman(num: number): string {
	if (num <= 26) {
		// Convert to letter: 1 -> a, 2 -> b, etc.
		return String.fromCharCode(96 + num); // 96 is 'a' - 1
	} else {
		// Convert to roman numeral
		return numberToRoman(num - 26); // Subtract 26 since we already used a-z
	}
}


function numberToRoman(num: number): string {
	const values = [1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1];
	const numerals = ['M', 'CM', 'D', 'CD', 'C', 'XC', 'L', 'XL', 'X', 'IX', 'V', 'IV', 'I'];
	
	let result = '';
	for (let i = 0; i < values.length; i++) {
		while (num >= values[i]) {
			result += numerals[i];
			num -= values[i];
		}
	}
	return result;
}

