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

export type DtTeamFormEntry = {
    entry: LongTermFormEntry;
    results: Record<JudgeType, Record<number, number | string | null>>;
    noElement: boolean;
    nestedEntries?: DtTeamFormEntry[];
};

export type TeamForm = {
    performanceId: number;
    teamName: string;
    cityName: string;
    problem: number;
    age: number;
    isFo: boolean;
    dtEntries: DtTeamFormEntry[];
    styleEntries: Array<{
        entry: StyleFormEntry;
        results: Record<JudgeType, Record<number, number | string | null>>;
        styleName?: string | null;
    }>;
    penaltyEntries: Array<{
        entry: PenaltyFormEntry;
        result: number | string | null;
        zeroBalsa?: boolean;
    }>;
};

export type PerformanceResult = {
    entryId: number;
    judgeType: JudgeType;
    judge: number;
    result: number;
    noElement?: boolean;
    styleName?: string | null;
    zeroBalsa?: boolean;
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
    results: Record<JudgeType, Record<number, number | string | null>>,
    noElement: boolean = false
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
                        result: numValue,
                        ...(noElement ? { noElement: true } : {})
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
        const results: PerformanceResult[] = [];
        // Process main entry
        if (dtEntry.entry.id != null) {
            results.push(...processEntryResults(dtEntry.entry.id, dtEntry.results, dtEntry.noElement));
        }
        // Process nested entries recursively
        if (dtEntry.nestedEntries && dtEntry.nestedEntries.length > 0) {
            results.push(...processDtEntries(dtEntry.nestedEntries));
        }
        return results;
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
            return Object.entries(styleEntry.results).flatMap(([judgeType, judgeMap]) => {
                return Object.entries(judgeMap)
                    .map(([judge, value]) => {
                        const numValue = toNumberOrNull(value);
                        if (numValue != null) {
                            const result: PerformanceResult = {
                                entryId: styleEntry.entry.id!,
                                judgeType: judgeType as JudgeType,
                                judge: Number(judge),
                                result: numValue
                            };
                            if (styleEntry.styleName != null) {
                                result.styleName = styleEntry.styleName;
                            }
                            return result;
                        }
                        return null;
                    })
                    .filter((result): result is PerformanceResult => result !== null);
            });
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
        .filter(penaltyEntry => {
            // For ZERO_BALSA, include if zeroBalsa is true
            if (penaltyEntry.entry.penaltyType === 'ZERO_BALSA') {
                return penaltyEntry.entry.id != null && penaltyEntry.zeroBalsa === true;
            }
            // For other types, include if result is not null
            return penaltyEntry.entry.id != null && penaltyEntry.result != null;
        })
        .map(penaltyEntry => {
            if (penaltyEntry.entry.penaltyType === 'ZERO_BALSA') {
                return {
                    entryId: penaltyEntry.entry.id!,
                    judgeType: 'STYLE' as JudgeType,
                    judge: 1,
                    result: 0,
                    zeroBalsa: true
                };
            }
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

