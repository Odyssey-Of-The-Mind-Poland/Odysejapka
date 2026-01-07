export type ProblemForm = {
	dtEntries: FormEntryType[];
	styleEntries: FormEntryType[];
	penaltyEntries: FormEntryType[];
};

export type FormEntryType = {
	id: number | null;
	name: string;
	type: 'SCORING' | 'SECTION' | 'SCORING_GROUP';
	scoring?: ScoringData | null;
	scoringGroup?: ScoringGroupData | null;
	entries: FormEntryType[];
};

export type ScoringData = {
	scoringType: 'SUBJECTIVE' | 'OBJECTIVE';
	pointsMin: number;
	pointsMax: number;
	judges: 'A' | 'B' | 'A_PLUS_B';
	noElement: boolean;
};

export type ScoringGroupData = {
	pointsMin: number;
	pointsMax: number;
};
