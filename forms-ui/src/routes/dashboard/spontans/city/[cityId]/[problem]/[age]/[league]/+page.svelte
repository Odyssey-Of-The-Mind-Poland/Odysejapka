<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import * as Table from "$lib/components/ui/table/index.js";
    import {Input} from "$lib/components/ui/input/index.js";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Badge} from "$lib/components/ui/badge/index.js";
    import {onMount} from "svelte";
    import {page} from "$app/state";
    import IconSparkles from "@tabler/icons-svelte/icons/sparkles";
    import {toast} from "svelte-sonner";

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

    type SpontanResultEntry = {
        judge: number;
        field: string;
        value: number;
    };

    type SpontanTeamResult = {
        performanceId: number;
        team: string;
        spontanHour: string;
        entries: SpontanResultEntry[];
        rawSpontan: number | null;
    };

    type SpontanGroupTeams = {
        spontanDefinition: SpontanDefinition;
        judgeCount: number;
        teams: SpontanTeamResult[];
    };

    let cityId = $derived(Number(page.params.cityId));
    let problem = $derived(Number(page.params.problem));
    let age = $derived(Number(page.params.age));
    let league = $derived(page.params.league || '');

    let citiesQuery = createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    });

    let cityName = $derived.by(() => {
        if (!citiesQuery.data) return '...';
        return citiesQuery.data.find((c: City) => c.id === cityId)?.name ?? '...';
    });

    let groupTeamsQuery = createOdysejaQuery<SpontanGroupTeams>({
        queryKey: ['spontanGroupTeams', String(cityId), String(problem), String(age), league],
        path: `/api/v1/spontan/result/${cityId}/teams?problem=${problem}&age=${age}&league=${encodeURIComponent(league)}`,
    });

    let definition = $derived(groupTeamsQuery.data?.spontanDefinition);
    let judgeCount = $derived(groupTeamsQuery.data?.judgeCount ?? 3);
    let teams = $derived(groupTeamsQuery.data?.teams ?? []);

    // Editable state: Map<performanceId, Map<"judge-field", value>>
    let editState = $state<Record<number, Record<string, number>>>({});
    let dirtySet = $state<Set<number>>(new Set());

    // Initialize edit state when data loads
    $effect(() => {
        if (teams.length > 0) {
            const newState: Record<number, Record<string, number>> = {};
            for (const team of teams) {
                const teamState: Record<string, number> = {};
                for (const entry of team.entries) {
                    teamState[`${entry.judge}-${entry.field}`] = entry.value;
                }
                newState[team.performanceId] = teamState;
            }
            editState = newState;
            dirtySet = new Set();
        }
    });

    function getVal(perfId: number, judge: number, field: string): number {
        return editState[perfId]?.[`${judge}-${field}`] ?? 0;
    }

    function setVal(perfId: number, judge: number, field: string, value: number) {
        if (!editState[perfId]) editState[perfId] = {};
        editState[perfId][`${judge}-${field}`] = value;
        dirtySet = new Set([...dirtySet, perfId]);
    }

    let saveMutation = createPutMutation<SpontanTeamResult, { performanceId: number; body: { entries: SpontanResultEntry[] } }>({
        path: (vars) => `/api/v1/spontan/result/${vars.performanceId}`,
        queryKey: ['spontanGroupTeams'],
        onSuccess: (data) => {
            dirtySet.delete(data.performanceId);
            dirtySet = new Set(dirtySet);
            toast.success(`Zapisano wynik: ${data.rawSpontan?.toFixed(2) ?? '—'}`);
        },
    });

    function saveTeam(perfId: number) {
        const teamEntries = editState[perfId];
        if (!teamEntries) return;

        const entries: SpontanResultEntry[] = [];
        for (const [key, value] of Object.entries(teamEntries)) {
            const [judgeStr, ...fieldParts] = key.split('-');
            entries.push({
                judge: Number(judgeStr),
                field: fieldParts.join('-'),
                value: value,
            });
        }

        saveMutation.mutate({
            performanceId: perfId,
            body: {entries},
        });
    }

    function judges(): number[] {
        return Array.from({length: judgeCount}, (_, i) => i + 1);
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
    {:else if teams.length > 0 && definition}
        {#each teams as team (team.performanceId)}
            <div class="rounded-xl border bg-card shadow-sm overflow-hidden">
                <div class="px-4 py-3 bg-muted/40 flex items-center justify-between">
                    <div class="flex items-center gap-3">
                        <span class="font-semibold">{team.team}</span>
                        <span class="text-sm text-muted-foreground font-mono">{team.spontanHour}</span>
                    </div>
                    <div class="flex items-center gap-3">
                        {#if team.rawSpontan != null}
                            <Badge variant="secondary" class="font-mono tabular-nums">
                                Wynik: {team.rawSpontan.toFixed(2)}
                            </Badge>
                        {/if}
                        <Button
                                size="sm"
                                onclick={() => saveTeam(team.performanceId)}
                                disabled={!dirtySet.has(team.performanceId) || saveMutation.isPending}
                        >
                            Zapisz
                        </Button>
                    </div>
                </div>

                <div class="p-4">
                    {#if definition.type === 'VERBAL'}
                        <Table.Root>
                            <Table.Header>
                                <Table.Row>
                                    <Table.Head class="font-semibold w-40"></Table.Head>
                                    {#each judges() as j}
                                        <Table.Head class="font-semibold text-center">Sędzia {j}</Table.Head>
                                    {/each}
                                </Table.Row>
                            </Table.Header>
                            <Table.Body>
                                <Table.Row>
                                    <Table.Cell class="font-medium">Odpowiedzi kreatywne</Table.Cell>
                                    {#each judges() as j}
                                        <Table.Cell>
                                            <Input
                                                    type="number"
                                                    class="w-20 text-center"
                                                    value={getVal(team.performanceId, j, 'creative')}
                                                    oninput={(e: Event) => setVal(team.performanceId, j, 'creative', Number((e.target as HTMLInputElement).value))}
                                            />
                                        </Table.Cell>
                                    {/each}
                                </Table.Row>
                                <Table.Row>
                                    <Table.Cell class="font-medium">Odpowiedzi pospolite</Table.Cell>
                                    {#each judges() as j}
                                        <Table.Cell>
                                            <Input
                                                    type="number"
                                                    class="w-20 text-center"
                                                    value={getVal(team.performanceId, j, 'common')}
                                                    oninput={(e: Event) => setVal(team.performanceId, j, 'common', Number((e.target as HTMLInputElement).value))}
                                            />
                                        </Table.Cell>
                                    {/each}
                                </Table.Row>
                            </Table.Body>
                        </Table.Root>
                    {:else}
                        <Table.Root>
                            <Table.Header>
                                <Table.Row>
                                    <Table.Head class="font-semibold w-48"></Table.Head>
                                    {#each judges() as j}
                                        <Table.Head class="font-semibold text-center">Sędzia {j}</Table.Head>
                                    {/each}
                                </Table.Row>
                            </Table.Header>
                            <Table.Body>
                                <Table.Row>
                                    <Table.Cell class="font-medium">Kreatywność rozwiązania</Table.Cell>
                                    {#each judges() as j}
                                        <Table.Cell>
                                            <Input
                                                    type="number"
                                                    class="w-20 text-center"
                                                    value={getVal(team.performanceId, j, 'creativity')}
                                                    oninput={(e: Event) => setVal(team.performanceId, j, 'creativity', Number((e.target as HTMLInputElement).value))}
                                            />
                                        </Table.Cell>
                                    {/each}
                                </Table.Row>
                                <Table.Row>
                                    <Table.Cell class="font-medium">Współpraca drużyny</Table.Cell>
                                    {#each judges() as j}
                                        <Table.Cell>
                                            <Input
                                                    type="number"
                                                    class="w-20 text-center"
                                                    value={getVal(team.performanceId, j, 'teamwork')}
                                                    oninput={(e: Event) => setVal(team.performanceId, j, 'teamwork', Number((e.target as HTMLInputElement).value))}
                                            />
                                        </Table.Cell>
                                    {/each}
                                </Table.Row>
                            </Table.Body>
                        </Table.Root>

                        {#if definition.fields.length > 0}
                            <div class="mt-4 border-t pt-4">
                                <h3 class="text-sm font-semibold mb-2">Pola formularza</h3>
                                <Table.Root>
                                    <Table.Header>
                                        <Table.Row>
                                            <Table.Head class="font-semibold w-48">Pole</Table.Head>
                                            <Table.Head class="font-semibold">Mnożnik</Table.Head>
                                            <Table.Head class="font-semibold">Wartość</Table.Head>
                                        </Table.Row>
                                    </Table.Header>
                                    <Table.Body>
                                        {#each definition.fields as field}
                                            <Table.Row>
                                                <Table.Cell class="font-medium">{field.name}</Table.Cell>
                                                <Table.Cell class="text-muted-foreground font-mono">x{field.multiplier}</Table.Cell>
                                                <Table.Cell>
                                                    <Input
                                                            type="number"
                                                            class="w-20 text-center"
                                                            value={getVal(team.performanceId, 0, field.name)}
                                                            oninput={(e: Event) => setVal(team.performanceId, 0, field.name, Number((e.target as HTMLInputElement).value))}
                                                    />
                                                </Table.Cell>
                                            </Table.Row>
                                        {/each}
                                    </Table.Body>
                                </Table.Root>
                            </div>
                        {/if}
                    {/if}
                </div>
            </div>
        {/each}
    {:else}
        <div class="rounded-lg border border-dashed p-12 text-center">
            <IconSparkles class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
            <p class="text-muted-foreground font-medium">Brak drużyn</p>
            <p class="text-sm text-muted-foreground/70 mt-1">W tej grupie nie ma drużyn.</p>
        </div>
    {/if}
</div>
