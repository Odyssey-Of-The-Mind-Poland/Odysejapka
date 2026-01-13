export type ProblemForm = {
	dtEntries: FormEntryType[];
	styleEntries: FormEntryType[];
	penaltyEntries: FormEntryType[];
	smallJudgesTeam?: number[] | null;
	bigJudgesTeam?: number[] | null;
};

export type FormEntryType = {
	id: number | null;
	name: string;
	type: 'SCORING' | 'SECTION' | 'SCORING_GROUP' | 'STYLE' | 'PENALTY';
	scoring?: ScoringData | null;
	scoringGroup?: ScoringGroupData | null;
	styleType?: 'PREDEFINED' | 'FREE_TEAM_CHOICE';
	penaltyType?: 'RANGE' | 'DISCRETE' | 'SINGLE' | 'ZERO_BALSA' | null;
	penaltyRange?: RangeData | null;
	penaltyDiscrete?: DiscreteData | null;
	penaltySingle?: SingleData | null;
	entries: FormEntryType[];
	sortIndex: number;
};

export function defaultEntry(
	type: 'SCORING' | 'SECTION' | 'SCORING_GROUP' | 'STYLE' | 'PENALTY',
	penaltyType?: 'RANGE' | 'DISCRETE' | 'SINGLE' | 'ZERO_BALSA'
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
						noElementEnabled: false
					}
				}
			: type === 'SCORING_GROUP'
				? {
						scoringGroup: {
							pointsMin: 0,
							pointsMax: 100
						}
					}
			: type === 'STYLE'
				? {
						styleType: 'PREDEFINED'
					}
				: type === 'PENALTY'
					? penaltyType === 'ZERO_BALSA'
						? {
								penaltyType: 'ZERO_BALSA'
							}
						: penaltyType === 'RANGE'
							? {
									penaltyType: 'RANGE',
									penaltyRange: { min: 0, max: 10 }
								}
							: penaltyType === 'DISCRETE'
								? {
										penaltyType: 'DISCRETE',
										penaltyDiscrete: { values: [0] }
									}
								: {
										penaltyType: 'SINGLE',
										penaltySingle: { value: 0 }
									}
					: {})
	};
}

export type ScoringData = {
	scoringType: 'SUBJECTIVE' | 'OBJECTIVE';
	pointsMin: number;
	pointsMax: number;
	judges: 'A' | 'B' | 'A_PLUS_B';
	noElementEnabled: boolean;
	subjectiveRange?: string | undefined;
	objectiveBucket?: string | undefined;
};

export type ScoringGroupData = {
	pointsMin: number;
	pointsMax: number;
};

export type RangeData = {
	min: number;
	max: number;
};

export type DiscreteData = {
	values: number[];
};

export type SingleData = {
	value: number;
};

export type City = {
	id: number;
	name: string;
};