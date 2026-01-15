export type LongTermFormEntry = {
    id: number | null;
    name: string;
    type: 'SCORING' | 'SECTION' | 'SCORING_GROUP';
    scoring?: {
        scoringType: 'SUBJECTIVE' | 'OBJECTIVE';
        noElementEnabled: boolean;
        judges: 'A' | 'B' | 'A_PLUS_B';
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

export type JudgeType = 'DT_A' | 'DT_B' | 'STYLE';

export type TeamForm = {
    performanceId: number;
    teamName: string;
    cityName: string;
    problem: number;
    age: number;
    isFo: boolean;
    dtEntries: Array<{
        entry: LongTermFormEntry;
        results: Record<JudgeType, Record<number, number | string | null>>;
    }>;
    styleEntries: Array<{
        entry: StyleFormEntry;
        results: Record<JudgeType, Record<number, number | string | null>>;
    }>;
    penaltyEntries: Array<{
        entry: PenaltyFormEntry;
        result: number | string | null;
    }>;
};

export type PerformanceResult = {
    entryId: number;
    judgeType: JudgeType;
    judge: number;
    result: number;
};

export type PerformanceResultsRequest = {
    results: PerformanceResult[];
};

/**
 * Converts a value to a number or returns null if invalid
 */
function toNumberOrNull(value: number | string | null | undefined): number | null {
    if (value === null || value === undefined || value === '') return null;
    const num = typeof value === 'string' ? Number(value) : value;
    return isNaN(num) ? null : num;
}

/**
 * Processes results from a single entry and returns an array of results
 */
function processEntryResults(
    entryId: number,
    results: Record<JudgeType, Record<number, number | string | null>>
): PerformanceResult[] {
    return Object.entries(results).flatMap(([judgeType, judgeMap]) => {
        return Object.entries(judgeMap)
            .map(([judge, value]) => {
                const numValue = toNumberOrNull(value);
                if (numValue != null) {
                    return {
                        entryId,
                        judgeType: judgeType as JudgeType,
                        judge: Number(judge),
                        result: numValue
                    };
                }
                return null;
            })
            .filter((result): result is PerformanceResult => result !== null);
    });
}

/**
 * Processes DT entries and returns an array of results
 */
function processDtEntries(
    dtEntries: TeamForm['dtEntries']
): PerformanceResult[] {
    return dtEntries.flatMap(dtEntry => {
        if (dtEntry.entry.id != null) {
            return processEntryResults(dtEntry.entry.id, dtEntry.results);
        }
        return [];
    });
}

/**
 * Processes style entries and returns an array of results
 */
function processStyleEntries(
    styleEntries: TeamForm['styleEntries']
): PerformanceResult[] {
    return styleEntries.flatMap(styleEntry => {
        if (styleEntry.entry.id != null) {
            return processEntryResults(styleEntry.entry.id, styleEntry.results);
        }
        return [];
    });
}

/**
 * Processes penalty entries and returns an array of results
 * Uses default values: STYLE judge type and judge 1
 */
function processPenaltyEntries(
    penaltyEntries: TeamForm['penaltyEntries']
): PerformanceResult[] {
    return penaltyEntries
        .filter(penaltyEntry => penaltyEntry.entry.id != null && penaltyEntry.result != null)
        .map(penaltyEntry => {
            const numValue = toNumberOrNull(penaltyEntry.result);
            if (numValue != null) {
                return {
                    entryId: penaltyEntry.entry.id!,
                    judgeType: 'STYLE' as JudgeType,
                    judge: 1,
                    result: numValue
                };
            }
            return null;
        })
        .filter((result): result is PerformanceResult => result !== null);
}

/**
 * Builds performance results array from team form data
 */
export function buildResults(formData: TeamForm | null): PerformanceResult[] {
    if (!formData) return [];
    
    const dtResults = processDtEntries(formData.dtEntries);
    const styleResults = processStyleEntries(formData.styleEntries);
    const penaltyResults = processPenaltyEntries(formData.penaltyEntries);
    
    return [...dtResults, ...styleResults, ...penaltyResults];
}

