<script lang="ts">
    import * as Input from "$lib/components/ui/input/index.js";
    import type {TeamForm, ValidationFailure, Anomaly} from "$lib/utils/form-results";
    import RangePenaltyInput from "./RangePenaltyInput.svelte";
    import DiscretePenaltyInput from "./DiscretePenaltyInput.svelte";
    import SinglePenaltyInput from "./SinglePenaltyInput.svelte";
    import ZeroBalsaPenaltyInput from "./ZeroBalsaPenaltyInput.svelte";
    import CircleAlertIcon from "@lucide/svelte/icons/circle-alert";
    import TriangleAlertIcon from "@lucide/svelte/icons/triangle-alert";

    let {
        penaltyEntry = $bindable(),
        validationErrors = [],
        anomalies = [],
    } = $props<{
        penaltyEntry: TeamForm['penaltyEntries'][0];
        validationErrors?: ValidationFailure[];
        anomalies?: Anomaly[];
    }>();

    let entryErrors = $derived(
        validationErrors.filter((e: ValidationFailure) => e.entryId === penaltyEntry.entry.id)
    );
    let hasError = $derived(entryErrors.length > 0);

    let entryAnomalies = $derived(
        anomalies.filter((a: Anomaly) => a.entryId === penaltyEntry.entry.id)
    );
    let hasAnomaly = $derived(entryAnomalies.length > 0);

    const isCommentEnabled = $derived.by(() => {
        if (penaltyEntry.entry.penaltyType === 'ZERO_BALSA') {
            return penaltyEntry.zeroBalsa === true;
        }
        const numValue = typeof penaltyEntry.result === 'number'
            ? penaltyEntry.result
            : (penaltyEntry.result ? Number(penaltyEntry.result) : 0);
        return numValue > 0;
    });

    const commentLength = $derived(penaltyEntry.comment?.length ?? 0);
    const maxCommentLength = 100;
</script>

<div class="flex items-center gap-4 px-5 py-3 transition-colors hover:bg-muted/30 group {hasError ? 'border-l-3 border-l-destructive bg-destructive/5' : hasAnomaly ? 'border-l-3 border-l-amber-500 bg-amber-500/5' : ''}">
    <!-- Left: Index + Name -->
    <div class="flex-1 min-w-0">
        <div class="flex items-start gap-2">
            <span class="text-xs text-muted-foreground font-mono tabular-nums shrink-0 pt-0.5">
                {penaltyEntry.entry.sortIndex}.
            </span>
            <div class="flex flex-col gap-0.5">
                <span class="text-sm font-medium text-foreground">
                    {penaltyEntry.entry.name}
                </span>
                {#if hasError}
                    <div class="flex items-center gap-1">
                        <CircleAlertIcon class="size-3 text-destructive shrink-0" />
                        <span class="text-xs text-destructive">{entryErrors[0].message}</span>
                    </div>
                {/if}
                {#if hasAnomaly}
                    <div class="flex items-center gap-1">
                        <TriangleAlertIcon class="size-3 text-amber-600 shrink-0" />
                        <span class="text-xs text-amber-600">{entryAnomalies[0].message}</span>
                    </div>
                {/if}
            </div>
        </div>
    </div>

    <!-- Right: Value + Comment -->
    <div class="flex items-start gap-3 shrink-0">
        <div>
            {#if penaltyEntry.entry.penaltyType === 'RANGE' && penaltyEntry.entry.penaltyRange}
                <RangePenaltyInput
                    bind:value={penaltyEntry.result}
                    min={penaltyEntry.entry.penaltyRange.min}
                    max={penaltyEntry.entry.penaltyRange.max}
                />
            {:else if penaltyEntry.entry.penaltyType === 'DISCRETE' && penaltyEntry.entry.penaltyDiscrete}
                <DiscretePenaltyInput
                    bind:value={penaltyEntry.result}
                    values={penaltyEntry.entry.penaltyDiscrete.values}
                />
            {:else if penaltyEntry.entry.penaltyType === 'SINGLE' && penaltyEntry.entry.penaltySingle}
                <SinglePenaltyInput
                    bind:value={penaltyEntry.result}
                    singleValue={penaltyEntry.entry.penaltySingle.value}
                />
            {:else if penaltyEntry.entry.penaltyType === 'ZERO_BALSA'}
                <ZeroBalsaPenaltyInput
                    bind:checked={penaltyEntry.zeroBalsa}
                />
            {/if}
        </div>

        <div class="flex flex-col gap-1 w-80">
            <span class="text-[10px] text-muted-foreground leading-none">Komentarz</span>
            <Input.Input
                type="text"
                bind:value={penaltyEntry.comment}
                class="w-full h-8 text-sm transition-colors"
                disabled={!isCommentEnabled}
                placeholder={isCommentEnabled ? "Komentarz..." : ""}
                maxlength={maxCommentLength}
            />
            {#if isCommentEnabled}
                <div class="text-[10px] text-muted-foreground text-right tabular-nums">
                    {commentLength}/{maxCommentLength}
                </div>
            {/if}
        </div>
    </div>
</div>
