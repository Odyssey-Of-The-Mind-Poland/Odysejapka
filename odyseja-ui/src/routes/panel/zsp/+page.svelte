<script lang="ts">
    import { onMount } from 'svelte';
    import { getZspTeams } from '$lib/apiService';
    import type { ZspResponse, ZspTeamGroup } from '$lib/types';
    import { ProgressBar, ProgressRadial, Tab, TabGroup } from '@skeletonlabs/skeleton';

    let zspData: ZspResponse | null = null;
    let isLoading = true;
    let activeTabValue = 0;
    let stageNumbers: number[] = [];
    
    async function loadData() {
        isLoading = true;
        try {
            zspData = await getZspTeams();
            stageNumbers = Object.keys(zspData.teams)
                .map(key => parseInt(key))
                .sort((a, b) => a - b);
            // Set active tab to first stage if it exists
            if (stageNumbers.length > 0) {
                activeTabValue = stageNumbers[0];
            }
        } catch (error) {
            console.error('Error loading ZSP data:', error);
        } finally {
            isLoading = false;
        }
    }
    
    onMount(() => {
        loadData();
        
        // Set up refresh interval (every 1 minute)
        const interval = setInterval(() => {
            loadData();
        }, 60000);
        
        // Clean up interval on component destroy
        return () => {
            clearInterval(interval);
        };
    });
    
    function getScoringProgress(stageGroups: ZspTeamGroup[]): number {
        if (!stageGroups || stageGroups.length === 0) return 0;
        
        const totalTeams = stageGroups.reduce((sum, group) => sum + group.allTeamsCount, 0);
        const scoredTeams = stageGroups.reduce((sum, group) => sum + group.scoredTeamsCount, 0);
        
        return totalTeams > 0 ? (scoredTeams / totalTeams) * 100 : 0;
    }
    
    function formatLeague(league: string): string {
        return league ? `Liga ${league}` : '';
    }
    
    function handleRefresh() {
        loadData();
    }
</script>

<div class="container mx-auto p-4">
    <div class="flex justify-between items-center mb-6">
        <h1 class="h2">ZSP - Zintegrowany System Punktacji</h1>
        
        <div class="flex items-center gap-4">
            {#if zspData}
                <span class="text-sm">Ostatnia aktualizacja: {zspData.lastFetchTime}</span>
            {/if}
            <button class="btn variant-filled-primary" on:click={handleRefresh}>
                Odśwież dane
            </button>
        </div>
    </div>
    
    {#if isLoading}
        <div class="flex justify-center my-8">
            <ProgressRadial width="w-12" stroke={100} meter="stroke-primary-500" track="stroke-primary-500/30" />
        </div>
    {:else if zspData && stageNumbers.length > 0}
        <TabGroup>
            {#each stageNumbers as stageNumber}
                <Tab bind:group={activeTabValue} name="scena{stageNumber}" value={stageNumber}>
                    <div class="flex gap-2 items-center">
                        <span>{stageNumber}</span>
                        {#if zspData.teams[stageNumber]}
                            <div class="w-20">
                                <ProgressBar 
                                    value={getScoringProgress(zspData.teams[stageNumber])} 
                                    max={100} 
                                    height="h-2" 
                                    meter="bg-primary-500" 
                                    track="bg-primary-500/30" 
                                />
                            </div>
                            <span class="text-xs">
                                {Math.round(getScoringProgress(zspData.teams[stageNumber]))}%
                            </span>
                        {/if}
                    </div>
                </Tab>
            {/each}
        </TabGroup>
        
        <div class="mt-4">
            {#if activeTabValue in zspData.teams}
                <div class="card p-4">
                    <div class="mb-4">
                        <h3 class="h3">Postęp punktacji dla Sceny {activeTabValue}</h3>
                        <div class="w-full mt-2">
                            <ProgressBar 
                                value={getScoringProgress(zspData.teams[activeTabValue])} 
                                max={100} 
                                height="h-4" 
                                meter="bg-primary-500" 
                                track="bg-primary-500/30" 
                            />
                            <div class="flex justify-between mt-1">
                                <span class="text-sm">
                                    Zespoły punktowane: {zspData.teams[activeTabValue].reduce((sum, group) => sum + group.scoredTeamsCount, 0)}
                                </span>
                                <span class="text-sm">
                                    Wszystkie zespoły: {zspData.teams[activeTabValue].reduce((sum, group) => sum + group.allTeamsCount, 0)}
                                </span>
                            </div>
                        </div>
                    </div>
                    
                    <table class="table table-compact">
                        <thead>
                            <tr>
                                <th>Problem</th>
                                <th>Gr. wiekowa</th>
                                <th>Liga</th>
                                <th>Liczba zespołów</th>
                                <th>Liczba punktowanych</th>
                                <th>Postęp</th>
                                <th>Akcje</th>
                            </tr>
                        </thead>
                        <tbody>
                            {#each zspData.teams[activeTabValue].sort((a, b) => {
                                if (a.problem !== b.problem) return a.problem - b.problem;
                                if (a.age !== b.age) return a.age - b.age;
                                return a.league.localeCompare(b.league);
                            }) as group}
                                <tr class="hover:bg-primary-500/20">
                                    <td>P{group.problem}</td>
                                    <td>G{group.age}</td>
                                    <td>{formatLeague(group.league)}</td>
                                    <td>{group.allTeamsCount}</td>
                                    <td>{group.scoredTeamsCount}</td>
                                    <td class="w-32">
                                        <ProgressBar 
                                            value={group.scoredTeamsCount} 
                                            max={group.allTeamsCount} 
                                            height="h-2" 
                                            meter="bg-primary-500" 
                                            track="bg-primary-500/30" 
                                        />
                                    </td>
                                    <td>
                                        <button class="btn btn-sm variant-soft" on:click={() => {
                                            // Toggle details for this group
                                            group.showDetails = !group.showDetails;
                                        }}>
                                            {#if group.showDetails}
                                                Ukryj szczegóły
                                            {:else}
                                                Pokaż szczegóły
                                            {/if}
                                        </button>
                                    </td>
                                </tr>
                                {#if group.showDetails}
                                    <tr>
                                        <td colspan="7">
                                            <div class="p-2 bg-surface-100-800-token">
                                                <table class="table table-compact w-full">
                                                    <thead>
                                                        <tr>
                                                            <th>Zespół</th>
                                                            <th>Miasto</th>
                                                            <th>Kod</th>
                                                            <th>Wynik</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        {#each group.teams.sort((a, b) => a.teamName.localeCompare(b.teamName)) as team}
                                                            <tr class={team.longTermScore ? 'bg-primary-500/10' : ''}>
                                                                <td>{team.teamName}</td>
                                                                <td>{team.city}</td>
                                                                <td>{team.code}</td>
                                                                <td>
                                                                    {#if team.longTermScore}
                                                                        {team.longTermScore.toFixed(2)}
                                                                    {:else}
                                                                        -
                                                                    {/if}
                                                                </td>
                                                            </tr>
                                                        {/each}
                                                    </tbody>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                {/if}
                            {/each}
                        </tbody>
                    </table>
                </div>
            {:else}
                <p>Nie znaleziono danych dla wybranej sceny.</p>
            {/if}
        </div>
    {:else}
        <div class="card p-4 text-center">
            <p>Brak danych ZSP. Sprawdź połączenie z serwerem lub skonfiguruj identyfikator ZSP.</p>
        </div>
    {/if}
</div> 