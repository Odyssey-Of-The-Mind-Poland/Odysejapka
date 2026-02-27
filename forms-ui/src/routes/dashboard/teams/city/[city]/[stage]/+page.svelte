<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import * as Table from "$lib/components/ui/table/index.js";
    import {onMount} from "svelte";
    import {goto} from "$app/navigation";
    import * as Input from "$lib/components/ui/input/index.js";
    import {Badge} from "$lib/components/ui/badge/index.js";
    import SearchIcon from "@lucide/svelte/icons/search";
    import UsersIcon from "@lucide/svelte/icons/users";
    import {page} from "$app/state";
    import RequirePermission from "$lib/components/RequirePermission.svelte";
    import CsvUploadDialog from "../CsvUploadDialog.svelte";
    import StageCredentialsButton from "./StageCredentialsButton.svelte";
    import ObsuwaBadge from "./ObsuwaBadge.svelte";
    import FormStateBadge from "./FormStateBadge.svelte";

    type City = {
        id: number;
        name: string;
    };

    type TeamListGroup = {
        group: {
            city: string;
            problem: number;
            age: number;
            stage: number;
            part: number;
            league: string;
        };
        performances: Array<{
            id: number;
            city: string;
            team: string;
            problem: number;
            age: number;
            stage: number;
            performance: string;
            performanceDay: string;
            league: string | null;
            actualPerformanceAt: string | null;
            formState: string;
            formStateLabel: string;
        }>;
    };

    type TeamInfo = {
        team: string;
        city: string;
        problem: number;
        age: number;
        stage: number;
        league: string | null;
        performanceId: number;
        expectedTime: string;
        actualTime: string | null;
        performanceDay: string;
        formState: string;
        formStateLabel: string;
    };

    type TeamGroup = {
        problem: number;
        age: number;
        league: string | null;
        teams: TeamInfo[];
    };

    type DayGroup = {
        day: string;
        groups: TeamGroup[];
    };

    let cityName = $derived(decodeURIComponent(page.params.city));
    let activeStage = $derived(Number(page.params.stage));

    let citiesQuery = createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    });

    let cityId = $derived.by(() => {
        if (!citiesQuery.data) return null;
        const found = citiesQuery.data.find((c: City) => c.name === cityName);
        return found?.id ?? null;
    });

    let performanceGroupsQuery = createOdysejaQuery<TeamListGroup[]>({
        queryKey: ['dashboardTeams'],
        path: '/api/v1/dashboard/teams',
    });

    let searchQuery = $state('');

    let allCityTeams = $derived.by(() => {
        if (!performanceGroupsQuery.data) return [];

        const teamList: TeamInfo[] = [];

        performanceGroupsQuery.data.forEach((group: TeamListGroup) => {
            group.performances.forEach((performance) => {
                if (performance.city === cityName) {
                    teamList.push({
                        team: performance.team,
                        city: performance.city,
                        problem: performance.problem,
                        age: performance.age,
                        stage: performance.stage,
                        league: performance.league,
                        performanceId: performance.id,
                        expectedTime: performance.performance,
                        actualTime: performance.actualPerformanceAt,
                        performanceDay: performance.performanceDay,
                        formState: performance.formState,
                        formStateLabel: performance.formStateLabel,
                    });
                }
            });
        });

        return teamList.sort((a, b) => a.expectedTime.localeCompare(b.expectedTime));
    });

    let stages = $derived.by(() => {
        const stageSet = new Set<number>();
        allCityTeams.forEach((t: TeamInfo) => stageSet.add(t.stage));
        return [...stageSet].sort((a, b) => a - b);
    });

    let teams = $derived(
        allCityTeams.filter((t: TeamInfo) => t.stage === activeStage)
    );

    let filteredTeams = $derived.by(() => {
        if (!searchQuery.trim()) return teams;
        const q = searchQuery.toLowerCase().trim();
        return teams.filter((t: TeamInfo) =>
            t.team.toLowerCase().includes(q) ||
            (t.league ?? '').toLowerCase().includes(q) ||
            String(t.problem).includes(q)
        );
    });

    const dayOrder: Record<string, number> = { 'sobota': 0, 'niedziela': 1 };

    let groupedByDay = $derived.by((): DayGroup[] => {
        const dayMap = new Map<string, Map<string, TeamInfo[]>>();

        for (const team of filteredTeams) {
            const day = team.performanceDay || 'inne';
            const groupKey = `${team.problem}-${team.age}-${team.league ?? ''}`;

            if (!dayMap.has(day)) dayMap.set(day, new Map());
            const groups = dayMap.get(day)!;
            if (!groups.has(groupKey)) groups.set(groupKey, []);
            groups.get(groupKey)!.push(team);
        }

        const days = [...dayMap.entries()]
            .sort(([a], [b]) => (dayOrder[a] ?? 99) - (dayOrder[b] ?? 99));

        return days.map(([day, groupsMap]) => {
            const groups = [...groupsMap.entries()]
                .map(([key, teams]) => {
                    const parts = key.split('-');
                    const problem = Number(parts[0]);
                    const age = Number(parts[1]);
                    const league = parts.slice(2).join('-') || null;
                    return { problem, age, league, teams } as TeamGroup;
                })
                .sort((a, b) => a.problem - b.problem || a.age - b.age || (a.league ?? '').localeCompare(b.league ?? ''));

            return { day, groups };
        });
    });

    function dayLabel(day: string): string {
        return day.charAt(0).toUpperCase() + day.slice(1);
    }

    function stageUrl(stage: number) {
        return `/dashboard/teams/city/${encodeURIComponent(cityName)}/${stage}`;
    }

    onMount(() => {
        setBreadcrumbs([
            {name: 'Drużyny', href: '/dashboard/teams'},
            {name: cityName, href: `/dashboard/teams/city/${encodeURIComponent(cityName)}`},
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex flex-col gap-1">
        <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
                <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                    <UsersIcon class="size-5 text-primary"/>
                </div>
                <div>
                    <h1 class="text-2xl font-semibold tracking-tight">{cityName}</h1>
                    <p class="text-sm text-muted-foreground">
                        {#if allCityTeams.length > 0}
                            {allCityTeams.length} {allCityTeams.length === 1 ? 'drużyna' : 'drużyn'}
                        {:else}
                            Drużyny w mieście {cityName}
                        {/if}
                    </p>
                </div>
            </div>
            {#if cityId !== null}
                <RequirePermission role="ADMINISTRATOR">
                    <CsvUploadDialog {cityId}/>
                </RequirePermission>
            {/if}
        </div>
    </div>

    {#if performanceGroupsQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie drużyn...</p>
        </div>
    {:else if performanceGroupsQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd podczas ładowania drużyn</p>
            <p class="text-sm text-muted-foreground mt-1">{String(performanceGroupsQuery.error)}</p>
        </div>
    {:else}
        <div class="flex items-center justify-between">
            <div class="flex gap-1 rounded-lg bg-muted p-1 w-fit">
                {#each stages as stage (stage)}
                    <a
                            href={stageUrl(stage)}
                            class="rounded-md px-3 py-1.5 text-sm font-medium transition-colors no-underline
                                {activeStage === stage
                                    ? 'bg-background text-foreground shadow-sm'
                                    : 'text-muted-foreground hover:text-foreground'}"
                    >
                        Scena {stage}
                    </a>
                {/each}
            </div>
            {#if cityId !== null}
                <RequirePermission role="ADMINISTRATOR">
                    <StageCredentialsButton {cityId} stage={activeStage}/>
                </RequirePermission>
            {/if}
        </div>

        {#if teams.length === 0 && !searchQuery.trim()}
            <div class="rounded-lg border border-dashed p-12 text-center">
                <UsersIcon class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
                <p class="text-muted-foreground font-medium">Brak drużyn</p>
                <p class="text-sm text-muted-foreground/70 mt-1">Na tej scenie nie ma jeszcze drużyn.</p>
            </div>
        {:else}
            <div class="relative">
                <SearchIcon class="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground"/>
                <Input.Input
                        type="text"
                        placeholder="Szukaj po drużynie, lidze lub problemie..."
                        class="pl-9"
                        bind:value={searchQuery}
                />
            </div>

            {#each groupedByDay as dayGroup (dayGroup.day)}
                <div class="flex flex-col gap-4">
                    <h2 class="text-xl font-semibold tracking-tight">{dayLabel(dayGroup.day)}</h2>

                    {#each dayGroup.groups as group (`${group.problem}-${group.age}-${group.league ?? ''}`)}
                        <div class="flex flex-col gap-1.5">
                            <div class="flex items-center gap-2 px-1">
                                <Badge variant="outline" class="font-mono tabular-nums">Problem {group.problem}</Badge>
                                <Badge variant="secondary" class="font-mono tabular-nums">Grupa wiekowa {group.age}</Badge>
                                {#if group.league}
                                    <Badge variant="outline">{group.league}</Badge>
                                {/if}
                                <span class="text-xs text-muted-foreground">{group.teams.length} {group.teams.length === 1 ? 'drużyna' : 'drużyn'}</span>
                            </div>
                            <div class="rounded-xl border bg-card shadow-sm overflow-hidden">
                                <Table.Root>
                                    <Table.Header>
                                        <Table.Row class="bg-muted/40 hover:bg-muted/40">
                                            <Table.Head class="font-semibold">Drużyna</Table.Head>
                                            <Table.Head class="font-semibold">Status</Table.Head>
                                            <Table.Head class="font-semibold">Godzina występu</Table.Head>
                                            <Table.Head class="font-semibold">Faktyczna godzina</Table.Head>
                                            <Table.Head class="font-semibold">Obsuwa</Table.Head>
                                        </Table.Row>
                                    </Table.Header>
                                    <Table.Body>
                                        {#each group.teams as team (team.performanceId)}
                                            <Table.Row
                                                    class="cursor-pointer transition-colors hover:bg-muted/50 group"
                                                    onclick={() => goto(`/dashboard/teams/city/${encodeURIComponent(cityName)}/${activeStage}/${team.performanceId}`)}
                                            >
                                                <Table.Cell class="font-medium">{team.team}</Table.Cell>
                                                <Table.Cell>
                                                    <FormStateBadge formState={team.formState} formStateLabel={team.formStateLabel} />
                                                </Table.Cell>
                                                <Table.Cell class="font-mono tabular-nums text-sm">
                                                    {team.expectedTime}
                                                </Table.Cell>
                                                <Table.Cell class="font-mono tabular-nums text-sm">
                                                    {#if team.actualTime}
                                                        {team.actualTime}
                                                    {:else}
                                                        <span class="text-muted-foreground">—</span>
                                                    {/if}
                                                </Table.Cell>
                                                <Table.Cell>
                                                    <ObsuwaBadge expectedTime={team.expectedTime} actualTime={team.actualTime} />
                                                </Table.Cell>
                                            </Table.Row>
                                        {/each}
                                    </Table.Body>
                                </Table.Root>
                            </div>
                        </div>
                    {/each}
                </div>
            {/each}

            {#if filteredTeams.length === 0 && searchQuery.trim()}
                <div class="py-8 text-center text-muted-foreground">
                    <p class="font-medium">Brak wyników</p>
                    <p class="text-sm mt-1">Spróbuj innej frazy</p>
                </div>
            {/if}
        {/if}
    {/if}
</div>
