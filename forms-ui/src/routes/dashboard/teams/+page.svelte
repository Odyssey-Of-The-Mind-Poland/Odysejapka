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

    let performanceGroupsQuery = createOdysejaQuery<PerformanceGroup[]>({
        queryKey: ['performanceGroups'],
        path: '/api/v2/timeTable',
    });

    let searchQuery = $state('');

    let teams = $derived.by(() => {
        if (!performanceGroupsQuery.data) return [];

        const teamList: TeamInfo[] = [];

        performanceGroupsQuery.data.forEach((group) => {
            group.performances.forEach((performance) => {
                teamList.push({
                    team: performance.team,
                    city: performance.city,
                    problem: performance.problem,
                    age: performance.age,
                    stage: performance.stage,
                    league: performance.league,
                    performanceId: performance.id,
                });
            });
        });

        return teamList.sort((a, b) => {
            if (a.city !== b.city) return a.city.localeCompare(b.city);
            return a.team.localeCompare(b.team);
        });
    });

    let filteredTeams = $derived.by(() => {
        if (!searchQuery.trim()) return teams;
        const q = searchQuery.toLowerCase().trim();
        return teams.filter(t =>
            t.team.toLowerCase().includes(q) ||
            t.city.toLowerCase().includes(q) ||
            t.league.toLowerCase().includes(q) ||
            String(t.problem).includes(q)
        );
    });

    onMount(() => {
        setBreadcrumbs([
            {name: 'Teams', href: '/dashboard/teams'}
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex flex-col gap-1">
        <div class="flex items-center gap-3">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <UsersIcon class="size-5 text-primary" />
            </div>
            <div>
                <h1 class="text-2xl font-semibold tracking-tight">Teams</h1>
                <p class="text-sm text-muted-foreground">
                    {#if teams.length > 0}
                        {teams.length} {teams.length === 1 ? 'team' : 'teams'} registered
                    {:else}
                        Manage and score team performances
                    {/if}
                </p>
            </div>
        </div>
    </div>

    {#if performanceGroupsQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Loading teams...</p>
        </div>
    {:else if performanceGroupsQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Error loading teams</p>
            <p class="text-sm text-muted-foreground mt-1">{String(performanceGroupsQuery.error)}</p>
        </div>
    {:else if teams.length === 0}
        <div class="rounded-lg border border-dashed p-12 text-center">
            <UsersIcon class="size-10 text-muted-foreground/40 mx-auto mb-3" />
            <p class="text-muted-foreground font-medium">No teams found</p>
            <p class="text-sm text-muted-foreground/70 mt-1">Teams will appear here once they are registered.</p>
        </div>
    {:else}
        <div class="relative">
            <SearchIcon class="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
            <Input.Input
                type="text"
                placeholder="Search by team, city, league or problem..."
                class="pl-9"
                bind:value={searchQuery}
            />
        </div>

        <div class="rounded-xl border bg-card shadow-sm overflow-hidden">
            <Table.Root>
                <Table.Header>
                    <Table.Row class="bg-muted/40 hover:bg-muted/40">
                        <Table.Head class="font-semibold">Team</Table.Head>
                        <Table.Head class="font-semibold">City</Table.Head>
                        <Table.Head class="font-semibold">Problem</Table.Head>
                        <Table.Head class="font-semibold">Age</Table.Head>
                        <Table.Head class="font-semibold">Stage</Table.Head>
                        <Table.Head class="font-semibold">League</Table.Head>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each filteredTeams as team (team.performanceId)}
                        <Table.Row
                            class="cursor-pointer transition-colors hover:bg-muted/50 group"
                            onclick={() => goto(`/dashboard/teams/${team.performanceId}`)}
                        >
                            <Table.Cell class="font-medium">{team.team}</Table.Cell>
                            <Table.Cell>{team.city}</Table.Cell>
                            <Table.Cell>
                                <Badge variant="outline" class="font-mono tabular-nums">{team.problem}</Badge>
                            </Table.Cell>
                            <Table.Cell>
                                <Badge variant="secondary" class="font-mono tabular-nums">{team.age}</Badge>
                            </Table.Cell>
                            <Table.Cell>
                                <span class="tabular-nums">{team.stage}</span>
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
                    <p class="font-medium">No results found</p>
                    <p class="text-sm mt-1">Try a different search term</p>
                </div>
            {/if}
        </div>
    {/if}
</div>
