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

    type City = {
        id: number;
        name: string;
    };

    type PerformanceGroup = {
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
            spontan: string;
            part: number;
            performanceDay: string;
            spontanDay: string;
            league: string;
            zspRow: number | null;
            zspSheet: string | null;
            performanceDate: string | null;
        }>;
    };

    type TeamInfo = {
        team: string;
        city: string;
        problem: number;
        age: number;
        stage: number;
        league: string;
        performanceId: number;
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

    let performanceGroupsQuery = createOdysejaQuery<PerformanceGroup[]>({
        queryKey: ['dashboardTeams'],
        path: '/api/v1/dashboard/teams',
    });

    let searchQuery = $state('');

    let allCityTeams = $derived.by(() => {
        if (!performanceGroupsQuery.data) return [];

        const teamList: TeamInfo[] = [];

        performanceGroupsQuery.data.forEach((group: PerformanceGroup) => {
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
                    });
                }
            });
        });

        return teamList.sort((a, b) => a.team.localeCompare(b.team));
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
            t.league.toLowerCase().includes(q) ||
            String(t.problem).includes(q)
        );
    });

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

            <div class="rounded-xl border bg-card shadow-sm overflow-hidden">
                <Table.Root>
                    <Table.Header>
                        <Table.Row class="bg-muted/40 hover:bg-muted/40">
                            <Table.Head class="font-semibold">Drużyna</Table.Head>
                            <Table.Head class="font-semibold">Problem</Table.Head>
                            <Table.Head class="font-semibold">Wiek</Table.Head>
                            <Table.Head class="font-semibold">Liga</Table.Head>
                        </Table.Row>
                    </Table.Header>
                    <Table.Body>
                        {#each filteredTeams as team (team.performanceId)}
                            <Table.Row
                                    class="cursor-pointer transition-colors hover:bg-muted/50 group"
                                    onclick={() => goto(`/dashboard/teams/city/${encodeURIComponent(cityName)}/${activeStage}/${team.performanceId}`)}
                            >
                                <Table.Cell class="font-medium">{team.team}</Table.Cell>
                                <Table.Cell>
                                    <Badge variant="outline" class="font-mono tabular-nums">{team.problem}</Badge>
                                </Table.Cell>
                                <Table.Cell>
                                    <Badge variant="secondary" class="font-mono tabular-nums">{team.age}</Badge>
                                </Table.Cell>
                                <Table.Cell>
                                    <Badge variant="outline">{team.league}</Badge>
                                </Table.Cell>
                            </Table.Row>
                        {/each}
                    </Table.Body>
                </Table.Root>

                {#if filteredTeams.length === 0 && searchQuery.trim()}
                    <div class="py-8 text-center text-muted-foreground">
                        <p class="font-medium">Brak wyników</p>
                        <p class="text-sm mt-1">Spróbuj innej frazy</p>
                    </div>
                {/if}
            </div>
        {/if}
    {/if}
</div>
