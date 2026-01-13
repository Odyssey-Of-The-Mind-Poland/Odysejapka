export type LongTermFormEntry = {
    id: number | null;
    name: string;
    type: 'SCORING' | 'SECTION' | 'SCORING_GROUP';
    scoring?: {
        scoringType: 'SUBJECTIVE' | 'OBJECTIVE';
        noElementEnabled: boolean;
        subjectiveRange?: string;
        objectiveBucket?: string;
    } | null;
    entries: LongTermFormEntry[];
    sortIndex: number;
};

export type StyleFormEntry = {
    id: number | null;
    name: string;
    type: 'STYLE';
    styleType: 'PREDEFINED' | 'FREE_TEAM_CHOICE';
    entries: StyleFormEntry[];
    sortIndex: number;
};

export type PenaltyFormEntry = {
    id: number | null;
    name: string;
    type: 'PENALTY';
    penaltyType?: 'RANGE' | 'DISCRETE' | 'SINGLE' | 'ZERO_BALSA' | null;
    penaltyRange?: { min: number; max: number } | null;
    penaltyDiscrete?: { values: number[] } | null;
    penaltySingle?: { value: number } | null;
    entries: PenaltyFormEntry[];
    sortIndex: number;
};

export type TeamForm = {
    performanceId: number;
    dtEntries: Array<{
        entry: LongTermFormEntry;
        judgesA: Record<number, number | string | null>;
        judgesB: Record<number, number | string | null>;
    }>;
    styleEntries: Array<{
        entry: StyleFormEntry;
        styleJudge: Record<number, number | string | null>;
    }>;
    penaltyEntries: Array<{
        entry: PenaltyFormEntry;
        penalty: Record<number, number | string | null>;
    }>;
};

export type PerformanceResult = {
    entryId: number;
    judge: number;
    result: number;
};

export type PerformanceResultsRequest = {
    results: PerformanceResult[];
};

