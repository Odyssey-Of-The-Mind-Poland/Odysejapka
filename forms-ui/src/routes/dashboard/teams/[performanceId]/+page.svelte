<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Badge} from "$lib/components/ui/badge/index.js";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {page} from "$app/state";
    import {onMount} from "svelte";
    import {toast} from "svelte-sonner";
    import {buildResults, type TeamForm, type PerformanceResultsRequest} from "$lib/utils/form-results";
    import DtEntriesTable from "./DtEntriesTable.svelte";
    import StyleEntriesTable from "./StyleEntriesTable.svelte";
    import PenaltyEntriesTable from "./PenaltyEntriesTable.svelte";
    import WeightHeldEntriesTable from "./WeightHeldEntriesTable.svelte";
    import FileTextIcon from "@lucide/svelte/icons/file-text";
    import SaveIcon from "@lucide/svelte/icons/save";
    import EyeIcon from "@lucide/svelte/icons/eye";
    import ArrowLeftIcon from "@lucide/svelte/icons/arrow-left";
    import {goto} from "$app/navigation";

    let performanceId = $derived(Number(page.params.performanceId));
    let performanceIdParam = $derived(page.params.performanceId);

    let teamFormQuery = $derived(createOdysejaQuery<TeamForm>({
        queryKey: ['teamForm', performanceIdParam],
        path: `/api/v1/form/${performanceIdParam}/result`,
    }));

    let formData = $state<TeamForm | null>(null);
    let savedResultsSnapshot = $state<string>('');

    let validationErrors = $derived(teamFormQuery.data?.validationErrors ?? []);
    let hasValidationErrors = $derived(validationErrors.length > 0);

    let currentResultsSnapshot = $derived(
        formData ? JSON.stringify(buildResults(formData)) : ''
    );
    let isDirty = $derived(currentResultsSnapshot !== savedResultsSnapshot);

    $effect(() => {
        if (teamFormQuery.data) {
            const data = { ...teamFormQuery.data, weightHeldEntries: teamFormQuery.data.weightHeldEntries ?? [] };
            formData = JSON.parse(JSON.stringify(data));
            savedResultsSnapshot = JSON.stringify(buildResults(data));
        }
    });

    let saveMutation = $derived(createPutMutation<unknown, PerformanceResultsRequest>({
        path: () => `/api/v1/form/${performanceIdParam}/result`,
        queryKey: ['teamForm', performanceIdParam],
        onSuccess: () => {
            toast.success('Wyniki drużyny zostały zapisane pomyślnie');
        }
    }));

    function handleSave() {
        if (!formData) return;

        const request = buildResults(formData);
        saveMutation.mutate(request);
    }

    onMount(() => {
        setBreadcrumbs([
            {name: 'Drużyny', href: '/dashboard/teams'},
            {name: `Drużyna ${performanceId}`, href: `/dashboard/teams/${performanceId}`}
        ]);
    });
</script>

<div class="flex flex-col gap-8">
    <!-- Header -->
    <div class="rounded-xl border bg-card shadow-sm overflow-hidden">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 px-6 py-5">
            <div class="flex items-center gap-4">
                <button
                    class="flex items-center justify-center size-10 rounded-full bg-primary text-primary-foreground hover:bg-primary/90 transition-colors shrink-0 cursor-pointer"
                    onclick={() => goto('/dashboard/teams')}
                >
                    <ArrowLeftIcon class="size-5" />
                </button>
                <div class="flex flex-col gap-1">
                    <div class="flex items-center gap-2 text-xs text-muted-foreground">
                        <span>Arkusz Długoterminowy</span>
                    </div>
                    {#if formData}
                        <h1 class="text-xl font-semibold tracking-tight">
                            {formData.teamName} – {formData.cityName}
                        </h1>
                        <div class="flex items-center gap-2 text-sm text-muted-foreground">
                            <span>Problem {formData.problem}: </span>
                            <span>·</span>
                            <span>{formData.age} Grupa wiekowa</span>
                        </div>
                    {:else}
                        <h1 class="text-xl font-semibold tracking-tight">
                            Formularz drużyny
                        </h1>
                    {/if}
                </div>
            </div>

            <div class="flex items-center gap-2 shrink-0">
                <Button
                    variant="outline"
                    onclick={() => window.location.href = `/dashboard/teams/${performanceIdParam}/preview`}
                    disabled={hasValidationErrors || isDirty}
                >
                    Podgląd
                </Button>
                <Button
                    onclick={handleSave}
                    disabled={saveMutation.isPending || !formData || !isDirty}
                >
                    <SaveIcon class="size-4" />
                    {saveMutation.isPending ? 'Zapisywanie...' : 'Zatwierdź arkusz'}
                </Button>
            </div>
        </div>
    </div>

    <!-- Content -->
    {#if teamFormQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie formularza...</p>
        </div>
    {:else if teamFormQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd ładowania formularza</p>
            <p class="text-sm text-muted-foreground mt-1">{String(teamFormQuery.error)}</p>
        </div>
    {:else if formData}
        <div class="flex flex-col gap-8">
            <DtEntriesTable bind:entries={formData.dtEntries} isFo={formData.isFo} {validationErrors} />
            <WeightHeldEntriesTable bind:entries={formData.weightHeldEntries} {validationErrors} />
            <StyleEntriesTable bind:entries={formData.styleEntries} />
            <PenaltyEntriesTable bind:entries={formData.penaltyEntries} {validationErrors} />

            {#if formData.dtEntries.length === 0 && formData.styleEntries.length === 0 && formData.penaltyEntries.length === 0 && formData.weightHeldEntries.length === 0}
                <div class="rounded-lg border border-dashed p-12 text-center">
                    <FileTextIcon class="size-10 text-muted-foreground/40 mx-auto mb-3" />
                    <p class="text-muted-foreground font-medium">Nie znaleziono wpisów</p>
                    <p class="text-sm text-muted-foreground/70 mt-1">Brak wpisów dla tej drużyny.</p>
                </div>
            {/if}
        </div>
    {/if}
</div>
