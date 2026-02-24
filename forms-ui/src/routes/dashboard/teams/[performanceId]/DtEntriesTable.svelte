<script lang="ts">
    import type {TeamForm, JudgeType, ValidationFailure} from "$lib/utils/form-results";
    import DtEntryRow from "./DtEntryRow.svelte";
    import ClipboardListIcon from "@lucide/svelte/icons/clipboard-list";
    import {Badge} from "$lib/components/ui/badge/index.js";

    const {
        entries = $bindable(),
        isFo = false,
        showHeader = true,
        nested = false,
        parentAllColumns,
        parentMaxJudgeCount,
        parentShowNoElementColumn,
        validationErrors = [],
    } = $props<{
        entries: TeamForm['dtEntries'];
        isFo: boolean;
        showHeader?: boolean;
        nested?: boolean;
        parentAllColumns?: Array<{ type: 'DT_A' | 'DT_B', judge: number }>;
        parentMaxJudgeCount?: number;
        parentShowNoElementColumn?: boolean;
        validationErrors?: ValidationFailure[];
    }>();

    function getJudgeKeys(results: Record<JudgeType, Record<number, number | string | null>>): number[] {
        const allKeys = new Set<number>();
        Object.values(results).forEach(judgeMap => {
            Object.keys(judgeMap).forEach(key => allKeys.add(Number(key)));
        });
        return Array.from(allKeys).sort((a, b) => a - b);
    }

    function getAllJudgeColumns(entries: TeamForm['dtEntries']): Array<{ type: 'DT_A' | 'DT_B', judge: number }> {
        const columnSet = new Set<string>();

        entries.forEach(entry => {
            const judgeKeys = getJudgeKeys(entry.results);
            judgeKeys.forEach(judgeKey => {
                if (entry.results.DT_A && entry.results.DT_A[judgeKey] !== undefined) {
                    columnSet.add(`DT_A-${judgeKey}`);
                }
                if (entry.results.DT_B && entry.results.DT_B[judgeKey] !== undefined) {
                    columnSet.add(`DT_B-${judgeKey}`);
                }
            });
        });

        const columns: Array<{ type: 'DT_A' | 'DT_B', judge: number }> = [];
        Array.from(columnSet).sort().forEach(key => {
            const [type, judge] = key.split('-');
            columns.push({type: type as 'DT_A' | 'DT_B', judge: Number(judge)});
        });

        return columns;
    }

    function getColumnLabel(type: 'DT_A' | 'DT_B', judge: number): string {
        const prefix = type === 'DT_A' ? 'Sędzia A' : 'Sędzia B';
        return `${prefix}${judge}`;
    }

    function getMaxJudgeCount(entries: TeamForm['dtEntries']): number {
        let maxJudge = 0;
        entries.forEach(entry => {
            Object.values(entry.results).forEach(judgeMap => {
                Object.keys(judgeMap).forEach(key => {
                    const judgeNum = Number(key);
                    if (judgeNum > maxJudge) {
                        maxJudge = judgeNum;
                    }
                });
            });
        });
        return maxJudge || 1;
    }

    function hasAnyNoElement(entries: TeamForm['dtEntries']): boolean {
        return entries.some(entry => {
            if (entry.entry.scoring?.noElementEnabled) return true;
            if (entry.nestedEntries) return hasAnyNoElement(entry.nestedEntries);
            return false;
        });
    }
</script>

{#if entries.length > 0}
    {@const allColumns = parentAllColumns ?? getAllJudgeColumns(entries)}
    {@const maxJudgeCount = parentMaxJudgeCount ?? getMaxJudgeCount(entries)}
    {@const showNoElementColumn = parentShowNoElementColumn ?? hasAnyNoElement(entries)}
    <div class="flex flex-col gap-3">
        {#if showHeader}
            <div class="flex items-center gap-2.5">
                <div class="flex items-center justify-center size-8 rounded-lg bg-secondary/10">
                    <ClipboardListIcon class="size-4 text-secondary" />
                </div>
                <div>
                    <h2 class="text-lg font-semibold tracking-tight">Punktacja długoterminowa</h2>
                    <p class="text-xs text-muted-foreground">{entries.length} {entries.length === 1 ? 'wpis' : 'wpisów'}</p>
                </div>
            </div>
        {/if}
        <div class={nested ? "flex flex-col divide-y divide-border" : "rounded-xl border bg-card shadow-sm overflow-hidden divide-y divide-border"}>
            {#if showHeader}
                <!-- Judge badges row aligned with selects -->
                <div class="flex items-start gap-4 px-5 py-2.5 bg-muted/40">
                    <div class="flex-1 min-w-0"></div>
                    <div class="flex items-start gap-2 shrink-0">
                        {#each allColumns as column}
                            <div class="w-[5.5rem] flex flex-col gap-1 items-center">
                                <span class="text-[10px] leading-none invisible">Punkty</span>
                                <Badge variant="default" class="text-xs">{getColumnLabel(column.type, column.judge)}</Badge>
                            </div>
                        {/each}
                        {#if showNoElementColumn}
                            <div class="w-[16rem] ml-2 pl-2 border-l border-border flex flex-col gap-1 items-center">
                                <span class="text-[10px] leading-none invisible">Punkty</span>
                                <span class="text-[10px] text-muted-foreground font-medium">Brak elementu</span>
                            </div>
                        {/if}
                    </div>
                </div>
            {/if}
            {#each entries as dtEntry, i (dtEntry.entry.id)}
                <DtEntryRow
                    bind:dtEntry={entries[i]}
                    allColumns={allColumns}
                    maxJudgeCount={maxJudgeCount}
                    isFo={isFo}
                    {showNoElementColumn}
                    {validationErrors}
                />
            {/each}
        </div>
    </div>
{/if}
