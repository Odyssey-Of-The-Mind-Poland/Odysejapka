<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {Button} from "$lib/components/ui/button/index.js";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {page} from "$app/state";
    import {toast} from "svelte-sonner";
    import {buildResults, type TeamForm, type PerformanceResultsRequest, type Anomaly, PERFORMANCE_AT_ENTRY_ID, PERFORMANCE_TIME_ENTRY_ID} from "$lib/utils/form-results";
    import * as Input from "$lib/components/ui/input/index.js";
    import * as Label from "$lib/components/ui/label/index.js";
    import DtEntriesTable from "./DtEntriesTable.svelte";
    import StyleEntriesTable from "./StyleEntriesTable.svelte";
    import PenaltyEntriesTable from "./PenaltyEntriesTable.svelte";
    import WeightHeldEntriesTable from "./WeightHeldEntriesTable.svelte";
    import FileTextIcon from "@lucide/svelte/icons/file-text";
    import SaveIcon from "@lucide/svelte/icons/save";
    import CheckIcon from "@lucide/svelte/icons/check";
    import ArrowLeftIcon from "@lucide/svelte/icons/arrow-left";
    import {goto} from "$app/navigation";
    import {currentUser} from "$lib/userStore";
    import {Switch} from "$lib/components/ui/switch/index.js";
    import ChatBubble from "./ChatBubble.svelte";
    import ChatPanel from "./ChatPanel.svelte";

    type City = {
        id: number;
        name: string;
    };

    let cityId = $derived(Number(page.params.cityId));
    let stageParam = $derived(page.params.stage);
    let performanceId = $derived(Number(page.params.performanceId));
    let performanceIdParam = $derived(page.params.performanceId);

    let isChatOpen = $state(false);

    let citiesQuery = createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    });

    let cityName = $derived.by(() => {
        if (!citiesQuery.data) return '...';
        return citiesQuery.data.find((c: City) => c.id === cityId)?.name ?? '...';
    });

    let stageUrl = $derived(`/dashboard/competitions/${cityId}/dt/${stageParam}`);
    let teamUrl = $derived(`${stageUrl}/${performanceIdParam}`);

    let teamFormQuery = $derived(createOdysejaQuery<TeamForm>({
        queryKey: ['teamForm', performanceIdParam],
        path: `/api/v1/form/${performanceIdParam}/result`,
    }));

    let formData = $state<TeamForm | null>(null);
    let savedResultsSnapshot = $state<string>('');

    let validationErrors = $derived(teamFormQuery.data?.validationErrors ?? []);
    let anomalies = $derived(teamFormQuery.data?.anomalies ?? []);
    let hasValidationErrors = $derived(validationErrors.length > 0);
    let performanceAtError = $derived(validationErrors.find(e => e.entryId === PERFORMANCE_AT_ENTRY_ID));
    let performanceTimeError = $derived(validationErrors.find(e => e.entryId === PERFORMANCE_TIME_ENTRY_ID));

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

    let user = $derived($currentUser);
    let problemNumber = $derived(teamFormQuery.data?.problem);
    let canApprove = $derived(
        (user?.roles?.some(r => r === 'ADMINISTRATOR') ||
        (user?.roles?.includes('KAPITAN') && problemNumber != null && user?.roles?.includes(`PROBLEM_${problemNumber}` as any)))
        ?? false
    );
    let isApproved = $derived(teamFormQuery.data?.approved ?? false);

    let approveMutation = $derived(createPutMutation<unknown, null>({
        path: () => `/api/v1/form/${performanceIdParam}/approve`,
        queryKey: ['teamForm', performanceIdParam],
        onSuccess: () => {
            toast.success('Arkusz został zatwierdzony');
        }
    }));

    let ranatraMutation = $derived(createPutMutation<{ ranatra: boolean }, null>({
        path: () => `/api/v1/form/${performanceIdParam}/ranatra`,
        queryKey: ['teamForm', performanceIdParam],
        onSuccess: (data) => {
            toast.success(data.ranatra ? 'Ranatra oznaczona' : 'Ranatra usunięta');
        }
    }));
    let isRanatra = $derived(teamFormQuery.data?.ranatra ?? false);

    function handleSave() {
        if (!formData) return;

        const request = buildResults(formData);
        saveMutation.mutate(request);
    }

    function handleApprove() {
        approveMutation.mutate(null);
    }

    function handleToggleRanatra() {
        ranatraMutation.mutate(null);
    }

    $effect(() => {
        setBreadcrumbs([
            {name: 'Konkursy', href: '/dashboard/competitions'},
            {name: cityName, href: `/dashboard/competitions/${cityId}/dt`},
            {name: `Scena ${stageParam}`, href: stageUrl},
            {name: `Drużyna ${performanceId}`, href: teamUrl}
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
                    onclick={() => goto(stageUrl)}
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
                    onclick={() => window.location.href = `${teamUrl}/preview`}
                    disabled={isDirty || !(teamFormQuery.data?.canPreview ?? false) || !isApproved}
                >
                    Podgląd
                </Button>
                {#if canApprove}
                    <Button
                        class="bg-green-600 text-white hover:bg-green-700"
                        onclick={handleApprove}
                        disabled={approveMutation.isPending || isDirty || isApproved || !(teamFormQuery.data?.canPreview ?? false)}
                    >
                        <CheckIcon class="size-4" />
                        {approveMutation.isPending ? 'Zatwierdzanie...' : 'Zatwierdź arkusz'}
                    </Button>
                {/if}
                <Button
                    onclick={handleSave}
                    disabled={saveMutation.isPending || !formData || !isDirty}
                >
                    <SaveIcon class="size-4" />
                    {saveMutation.isPending ? 'Zapisywanie...' : 'Zapisz'}
                </Button>
            </div>
        </div>
        {#if canApprove}
            <div class="flex items-center justify-end gap-2 px-6 pb-4">
                <Label.Root for="ranatra-toggle" class="text-sm {isRanatra ? 'text-primary font-medium' : 'text-muted-foreground'}">
                    Ranatra
                </Label.Root>
                <Switch
                    id="ranatra-toggle"
                    checked={isRanatra}
                    onCheckedChange={handleToggleRanatra}
                    disabled={ranatraMutation.isPending}
                />
            </div>
        {/if}
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
            <!-- Performance time and duration at top -->
            <div class="rounded-xl border bg-card shadow-sm p-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                    <div class="flex flex-col gap-2">
                        <Label.Root for="performanceAt">Godzina występu</Label.Root>
                        <Input.Input
                            id="performanceAt"
                            type="time"
                            class={performanceAtError ? "border-destructive focus-visible:ring-destructive" : ""}
                            bind:value={formData.performanceAt}
                        />
                        {#if performanceAtError}
                            <p class="text-sm text-destructive">{performanceAtError.message}</p>
                        {/if}
                    </div>
                    <div class="flex flex-col gap-2">
                        <Label.Root for="performanceTime">Czas trwania (min)</Label.Root>
                        <Input.Input
                            id="performanceTime"
                            type="number"
                            min="0"
                            step="1"
                            placeholder="np. 8"
                            class={performanceTimeError ? "border-destructive focus-visible:ring-destructive" : ""}
                            bind:value={formData.performanceTime}
                        />
                        {#if performanceTimeError}
                            <p class="text-sm text-destructive">{performanceTimeError.message}</p>
                        {/if}
                    </div>
                </div>
            </div>

            <DtEntriesTable bind:entries={formData.dtEntries} isFo={formData.isFo} {validationErrors} {anomalies} />
            <WeightHeldEntriesTable bind:entries={formData.weightHeldEntries} {validationErrors} />
            <StyleEntriesTable bind:entries={formData.styleEntries} {validationErrors} />
            <PenaltyEntriesTable bind:entries={formData.penaltyEntries} {validationErrors} {anomalies} />

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

<!-- Chat -->
{#if !isChatOpen}
    <ChatBubble unreadCount={0} onclick={() => isChatOpen = true} />
{/if}
{#if isChatOpen}
    <ChatPanel performanceId={performanceIdParam} onclose={() => isChatOpen = false} />
    <ChatBubble unreadCount={0} onclick={() => isChatOpen = false} />
{/if}
