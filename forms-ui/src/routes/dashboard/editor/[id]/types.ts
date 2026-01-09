export type ProblemForm = {
	dtEntries: FormEntryType[];
	styleEntries: FormEntryType[];
	penaltyEntries: FormEntryType[];
};

export type FormEntryType = {
	id: number | null;
	name: string;
	type: 'SCORING' | 'SECTION' | 'SCORING_GROUP' | 'STYLE' | 'PENALTY';
	scoring?: ScoringData | null;
	scoringGroup?: ScoringGroupData | null;
	entries: FormEntryType[];
	sortIndex: number;
};

export function defaultEntry(
	type: 'SCORING' | 'SECTION' | 'SCORING_GROUP' | 'STYLE' | 'PENALTY'
): FormEntryType {
	return {
		id: null,
		name: '',
		type,
		entries: [],
		sortIndex: 0,
		...(type === 'SCORING'
			? {
					scoring: {
						scoringType: 'SUBJECTIVE',
						pointsMin: 0,
						pointsMax: 100,
						judges: 'A',
						noElement: false
					}
				}
			: type === 'SCORING_GROUP'
				? {
						scoringGroup: {
							pointsMin: 0,
							pointsMax: 100
						}
					}
				: {})
	};
}

export type ScoringData = {
	scoringType: 'SUBJECTIVE' | 'OBJECTIVE';
	pointsMin: number;
	pointsMax: number;
	judges: 'A' | 'B' | 'A_PLUS_B';
	noElement: boolean;
	subjectiveRange?: string | undefined;
	objectiveBucket?: string | undefined;
};

export type ScoringGroupData = {
	pointsMin: number;
	pointsMax: number;
};
