<script lang="ts">
    import type { TeamForm, JudgeType, ValidationFailure } from "$lib/utils/form-results";
    import StyleEntryRow from "./StyleEntryRow.svelte";
    import PaletteIcon from "@lucide/svelte/icons/palette";
    import {Badge} from "$lib/components/ui/badge/index.js";

    const {
        entries = $bindable(),
        validationErrors = [],
    } = $props<{
        entries: TeamForm['styleEntries'];
        validationErrors?: ValidationFailure[];
    }>();

    function getAllJudgeColumns(entries: TeamForm['styleEntries']): number[] {
        const maxJudge = getMaxJudgeCount(entries);
        return Array.from({ length: maxJudge }, (_, i) => i + 1);
    }

    function getMaxJudgeCount(entries: TeamForm['styleEntries']): number {
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
</script>

{#if entries.length > 0}
    {@const allJudgeColumns = getAllJudgeColumns(entries)}
    <div class="flex flex-col gap-3">
        <div class="flex items-center gap-2.5">
            <div class="flex items-center justify-center size-8 rounded-lg bg-primary/10">
                <PaletteIcon class="size-4 text-primary" />
            </div>
            <div>
                <h2 class="text-lg font-semibold tracking-tight">Styl</h2>
                <p class="text-xs text-muted-foreground">{entries.length} {entries.length === 1 ? 'wpis' : 'wpisów'}</p>
            </div>
        </div>
        <div class="rounded-xl border bg-card shadow-sm overflow-hidden divide-y divide-border">
            <!-- Judge badges row aligned with selects -->
            <div class="flex items-center gap-4 px-5 py-2.5 bg-muted/40">
                <div class="flex-1 min-w-0"></div>
                <div class="flex items-center gap-4 shrink-0">
                    {#each allJudgeColumns as judge}
                        <div class="w-[5.5rem] flex justify-center">
                            <Badge variant="default" class="text-xs whitespace-nowrap">STYL {judge}</Badge>
                        </div>
                    {/each}
                </div>
            </div>
            {#each entries as styleEntry, i (styleEntry.entry.id)}
                <StyleEntryRow
                    bind:styleEntry={entries[i]}
                    allJudgeColumns={allJudgeColumns}
                    {validationErrors}
                />
            {/each}
        </div>
    </div>
{/if}
