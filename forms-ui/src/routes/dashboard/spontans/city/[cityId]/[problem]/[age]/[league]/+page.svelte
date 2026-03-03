<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Badge} from "$lib/components/ui/badge/index.js";
    import {onMount} from "svelte";
    import {page} from "$app/state";
    import IconSparkles from "@tabler/icons-svelte/icons/sparkles";
    import {toast} from "svelte-sonner";
    import SpontanTeamRow from "./SpontanTeamRow.svelte";

    type City = {
        id: number;
        name: string;
    };

    type SpontanFieldDef = {
        id?: number;
        name: string;
        multiplier: number;
    };

    type SpontanDefinition = {
        id: number;
        name: string;
        type: 'VERBAL' | 'MANUAL';
        multiplier?: number;
        fields: SpontanFieldDef[];
    };

    type VerbalJudgeEntry = {
        judge: number;
        creative: number;
        common: number;
    };

    type ManualJudgeEntry = {
        judge: number;
        creativity: number;
        teamwork: number;
    };

    type ManualScoreEntry = {
        field: string;
        value: number;
    };

    type SpontanTeamResult = {
        performanceId: number;
        team: string;
        spontanHour: string;
        verbalEntries: VerbalJudgeEntry[];
        manualJudgeEntries: ManualJudgeEntry[];
        manualEntries: ManualScoreEntry[];
        rawSpontan: number | null;
    };

    type SpontanGroupTeams = {
        spontanDefinition: SpontanDefinition;
        judgeCount: number;
        teams: SpontanTeamResult[];
    };

    type VerbalEntryForm = {
        creative: number;
        common: number;
    };

    type ManualJudgeEntryForm = {
        creativity: number;
        teamwork: number;
    };

    type SpontanFormTeam = {
        performanceId: number;
        team: string;
        spontanHour: string;
        rawSpontan: number | null;
        verbalEntries: VerbalEntryForm[];
        manualJudgeEntries: ManualJudgeEntryForm[];
        manualEntries: Record<string, number>;
    };

    let cityId = $derived(Number(page.params.cityId));
    let problem = $derived(Number(page.params.problem));
    let age = $derived(Number(page.params.age));
    let league = $derived(page.params.league === '_' ? '' : (page.params.league || ''));

    let citiesQuery = $derived(createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    }));

    let cityName = $derived.by(() => {
        if (!citiesQuery.data) return '...';
        return citiesQuery.data.find((c: City) => c.id === cityId)?.name ?? '...';
    });

    let groupTeamsQuery = $derived(createOdysejaQuery<SpontanGroupTeams>({
        queryKey: ['spontanGroupTeams', String(cityId), String(problem), String(age), league],
        path: `/api/v1/spontan/result/${cityId}/teams?problem=${problem}&age=${age}&league=${encodeURIComponent(league)}`,
    }));

    let definition = $derived(groupTeamsQuery.data?.spontanDefinition);
    let judgeCount = $derived(groupTeamsQuery.data?.judgeCount ?? 3);
    let teams = $derived(groupTeamsQuery.data?.teams ?? []);

    let formData = $state<SpontanFormTeam[] | null>(null);
    let savedSnapshots = $state<Record<number, string>>({});

    let isDirty = $derived.by(() => {
        if (!formData) return false;
        return formData.some(t =>
            JSON.stringify({v: t.verbalEntries, mj: t.manualJudgeEntries, m: t.manualEntries}) !== savedSnapshots[t.performanceId]
        );
    });

    function snapshot(team: SpontanFormTeam): string {
        return JSON.stringify({v: team.verbalEntries, mj: team.manualJudgeEntries, m: team.manualEntries});
    }

    $effect(() => {
        if (teams.length > 0 && definition) {
            const jCount = judgeCount;
            const isVerbal = definition.type === 'VERBAL';

            const data: SpontanFormTeam[] = teams.map(team => {
                const verbalEntries: VerbalEntryForm[] = [];
                const manualJudgeEntries: ManualJudgeEntryForm[] = [];

                for (let j = 0; j < jCount; j++) {
                    if (isVerbal) {
                        verbalEntries.push({creative: 0, common: 0});
                    } else {
                        manualJudgeEntries.push({creativity: 0, teamwork: 0});
                    }
                }

                for (const entry of team.verbalEntries) {
                    if (entry.judge >= 1 && entry.judge <= jCount) {
                        verbalEntries[entry.judge - 1] = {
                            creative: entry.creative ?? 0,
                            common: entry.common ?? 0,
                        };
                    }
                }
                for (const entry of team.manualJudgeEntries) {
                    if (entry.judge >= 1 && entry.judge <= jCount) {
                        manualJudgeEntries[entry.judge - 1] = {
                            creativity: entry.creativity ?? 0,
                            teamwork: entry.teamwork ?? 0,
                        };
                    }
                }

                const manualEntries: Record<string, number> = {};
                if (!isVerbal) {
                    for (const f of definition.fields) {
                        manualEntries[f.name] = 0;
                    }
                }
                for (const entry of team.manualEntries) {
                    manualEntries[entry.field] = entry.value;
                }

                return {
                    performanceId: team.performanceId,
                    team: team.team,
                    spontanHour: team.spontanHour,
                    rawSpontan: team.rawSpontan,
                    verbalEntries,
                    manualJudgeEntries,
                    manualEntries,
                };
            });

            const snaps: Record<number, string> = {};
            for (const t of data) {
                snaps[t.performanceId] = snapshot(t);
            }
            formData = data;
            savedSnapshots = snaps;
        }
    });

    type SaveRequest = {
        performanceId: number;
        body: {
            verbalEntries: VerbalJudgeEntry[];
            manualJudgeEntries: ManualJudgeEntry[];
            manualEntries: ManualScoreEntry[];
        };
    };

    let saveMutation = createPutMutation<SpontanTeamResult, SaveRequest>({
        path: (vars) => `/api/v1/spontan/result/${vars.performanceId}`,
        queryKey: ['spontanGroupTeams'],
        onSuccess: (data) => {
            toast.success(`Zapisano wynik: ${data.rawSpontan?.toFixed(2) ?? '—'}`);
        },
    });

    function buildRequest(team: SpontanFormTeam): SaveRequest {
        const verbalEntries: VerbalJudgeEntry[] = team.verbalEntries.map((entry, i) => ({
            judge: i + 1,
            ...entry,
        }));
        const manualJudgeEntries: ManualJudgeEntry[] = team.manualJudgeEntries.map((entry, i) => ({
            judge: i + 1,
            ...entry,
        }));
        const manualEntries: ManualScoreEntry[] = [];
        for (const [field, value] of Object.entries(team.manualEntries)) {
            manualEntries.push({field, value});
        }

        return {
            performanceId: team.performanceId,
            body: {verbalEntries, manualJudgeEntries, manualEntries},
        };
    }

    function judges(): number[] {
        return Array.from({length: judgeCount}, (_, i) => i + 1);
    }

    let manualFields = $derived(
        definition?.type === 'MANUAL' ? definition.fields : []
    );

    function saveAll() {
        if (!formData) return;
        for (const teamData of formData) {
            if (snapshot(teamData) !== savedSnapshots[teamData.performanceId]) {
                saveMutation.mutate(buildRequest(teamData));
            }
        }
    }

    onMount(() => {
        setBreadcrumbs([
            {name: 'Spontany', href: '/dashboard/spontans'},
            {name: cityName, href: `/dashboard/spontans/city/${cityId}`},
            {name: `P${problem} G${age}${league ? ` ${league}` : ''}`, href: ''},
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex flex-col gap-1">
        <div class="flex items-center gap-3">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconSparkles class="size-5 text-primary"/>
            </div>
            <div>
                <h1 class="text-2xl font-semibold tracking-tight">
                    {definition?.name ?? 'Spontan'} - {cityName}
                </h1>
                <p class="text-sm text-muted-foreground">
                    <Badge variant="outline" class="font-mono tabular-nums mr-1">Problem {problem}</Badge>
                    <Badge variant="secondary" class="font-mono tabular-nums mr-1">Grupa wiekowa {age}</Badge>
                    {#if league}
                        <Badge variant="outline" class="mr-1">{league}</Badge>
                    {/if}
                    {#if definition}
                        <Badge variant="default" class="text-xs">
                            {definition.type === 'VERBAL' ? 'Słowny' : 'Manualny'}
                        </Badge>
                    {/if}
                </p>
            </div>
        </div>
    </div>

    {#if groupTeamsQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie...</p>
        </div>
    {:else if groupTeamsQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd podczas ładowania</p>
            <p class="text-sm text-muted-foreground mt-1">{String(groupTeamsQuery.error)}</p>
        </div>
    {:else if formData && formData.length > 0 && definition}
        <div class="flex justify-end">
            <Button
                    onclick={saveAll}
                    disabled={!isDirty || saveMutation.isPending}
            >
                Zapisz wszystko
            </Button>
        </div>

        <div class="rounded-xl border bg-card shadow-sm overflow-x-auto">
            <table class="w-full text-sm">
                <thead>
                    <tr class="bg-muted/40 border-b">
                        <th rowspan="2" class="px-4 py-2 text-left font-semibold align-bottom">Drużyna</th>
                        {#if manualFields.length > 0}
                            {#each manualFields as field}
                                <th rowspan="2" class="px-2 py-2 text-center font-semibold border-l align-bottom">
                                    <div>{field.name}</div>
                                    <div class="text-xs text-muted-foreground font-normal">x{field.multiplier}</div>
                                </th>
                            {/each}
                        {/if}
                        {#each judges() as j}
                            <th colspan="2" class="px-2 py-2 text-center font-semibold border-l">
                                Sędzia {j}
                            </th>
                        {/each}
                        <th rowspan="2" class="px-4 py-2 text-center font-semibold border-l align-bottom">Wynik</th>
                    </tr>
                    <tr class="bg-muted/40 border-b">
                        {#each judges() as _j}
                            {#if definition.type === 'VERBAL'}
                                <th class="px-2 py-1 text-center text-xs font-medium text-muted-foreground border-l">Odp. kreatywne</th>
                                <th class="px-2 py-1 text-center text-xs font-medium text-muted-foreground">Odp. pospolite</th>
                            {:else}
                                <th class="px-2 py-1 text-center text-xs font-medium text-muted-foreground border-l">Kreatywność</th>
                                <th class="px-2 py-1 text-center text-xs font-medium text-muted-foreground">Współpraca</th>
                            {/if}
                        {/each}
                    </tr>
                </thead>
                <tbody>
                    {#each formData as teamData, i (teamData.performanceId)}
                        <SpontanTeamRow
                            bind:verbalEntries={formData[i].verbalEntries}
                            bind:manualJudgeEntries={formData[i].manualJudgeEntries}
                            bind:manualEntries={formData[i].manualEntries}
                            team={teamData.team}
                            spontanHour={teamData.spontanHour}
                            rawSpontan={teamData.rawSpontan}
                            judges={judges()}
                            type={definition.type}
                            {manualFields}
                            savedSnapshot={savedSnapshots[teamData.performanceId] ?? '{}'}
                        />
                    {/each}
                </tbody>
            </table>
        </div>
    {:else}
        <div class="rounded-lg border border-dashed p-12 text-center">
            <IconSparkles class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
            <p class="text-muted-foreground font-medium">Brak drużyn</p>
            <p class="text-sm text-muted-foreground/70 mt-1">W tej grupie nie ma drużyn.</p>
        </div>
    {/if}
</div>
