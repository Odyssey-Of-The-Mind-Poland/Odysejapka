<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import * as Table from "$lib/components/ui/table/index.js";
    import {onMount} from "svelte";

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
    };

    let performanceGroupsQuery = createOdysejaQuery<PerformanceGroup[]>({
        queryKey: ['performanceGroups'],
        path: '/api/v2/timeTable',
    });

    let teams = $derived.by(() => {
        if (!performanceGroupsQuery.data) return [];
        
        const teamMap = new Map<string, TeamInfo>();
        
        performanceGroupsQuery.data.forEach((group) => {
            group.performances.forEach((performance) => {
                const key = `${performance.team}-${performance.city}`;
                if (!teamMap.has(key)) {
                    teamMap.set(key, {
                        team: performance.team,
                        city: performance.city,
                        problem: performance.problem,
                        age: performance.age,
                        stage: performance.stage,
                        league: performance.league,
                    });
                }
            });
        });
        
        return Array.from(teamMap.values()).sort((a, b) => {
            if (a.city !== b.city) return a.city.localeCompare(b.city);
            return a.team.localeCompare(b.team);
        });
    });

    onMount(() => {
        setBreadcrumbs([
            {name: 'Teams', href: '/dashboard/teams'}
        ]);
    });
</script>

<div class="flex flex-col gap-4">
    <div class="flex justify-between items-center">
        <h1 class="text-2xl font-bold">Teams</h1>
    </div>

    {#if performanceGroupsQuery.isPending}
        <div class="flex justify-center items-center py-8">
            <Spinner size="sm"/>
        </div>
    {:else if performanceGroupsQuery.error}
        <div class="text-red-500">
            Error loading teams: {String(performanceGroupsQuery.error)}
        </div>
    {:else if teams.length === 0}
        <div class="text-gray-500">No teams found.</div>
    {:else}
        <div class="rounded-md border">
            <Table.Root>
                <Table.Header>
                    <Table.Row>
                        <Table.Head>Team</Table.Head>
                        <Table.Head>City</Table.Head>
                        <Table.Head>Problem</Table.Head>
                        <Table.Head>Age</Table.Head>
                        <Table.Head>Stage</Table.Head>
                        <Table.Head>League</Table.Head>
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each teams as team (team.team + team.city)}
                        <Table.Row>
                            <Table.Cell class="font-medium">{team.team}</Table.Cell>
                            <Table.Cell>{team.city}</Table.Cell>
                            <Table.Cell>{team.problem}</Table.Cell>
                            <Table.Cell>{team.age}</Table.Cell>
                            <Table.Cell>{team.stage}</Table.Cell>
                            <Table.Cell>{team.league}</Table.Cell>
                        </Table.Row>
                    {/each}
                </Table.Body>
            </Table.Root>
        </div>
    {/if}
</div>

