export type ProblemForm = {
	dtEntries: FormEntryType[];
	styleEntries: FormEntryType[];
	penaltyEntries: FormEntryType[];
};

export type FormEntryType = {
	id: number | null;
	name: string;
	type: 'PUNCTUATION' | 'SECTION' | 'PUNCTUATION_GROUP';
	punctuation?: PunctuationData | null;
	punctuationGroup?: PunctuationGroupData | null;
	entries: FormEntryType[];
};

export type PunctuationData = {
	punctuationType: 'SUBJECTIVE' | 'OBJECTIVE';
	pointsMin: number;
	pointsMax: number;
	judges: 'A' | 'B' | 'A_PLUS_B';
	noElement: boolean;
};

export type PunctuationGroupData = {
	pointsMin: number;
	pointsMax: number;
};
