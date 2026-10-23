<script lang="ts">
    import * as Dialog from '$lib/components/ui/dialog/index.js';
    import {Button} from '$lib/components/ui/button/index.js';
    import {Spinner} from '$lib/components/ui/spinner';
    import {createOdysejaQuery} from "$lib/queries";
    import type {City, JudgeTeam} from "./types";

    let {form = $bindable(), open = $bindable()} = $props();

    let cityQuery = createOdysejaQuery<City[]>({
        queryKey: ['city'],
        path: `/api/v1/city`,
        enabled: open
    });
    let cities = $derived(cityQuery.data);

    $effect(() => {
        if (open) {
            cityQuery.refetch?.()
        }
    });

    let judgeTeams = $derived<JudgeTeam[]>(form.judgeTeams ?? []);

    function updateJudgeTeam(cityId: number, field: keyof JudgeTeam["judgeCount"], e: Event) {
        judgeTeams = judgeTeams || [];
        const teamIndex = judgeTeams.findIndex(team => team.cityId === cityId);
        judgeTeams[teamIndex].judgeCount[field] = Number((e.target as HTMLInputElement).value) ?? 1;
    }

    function saveJudgeTeams() {
        form.judgeTeams = judgeTeams;
        open = false;
    }
</script>

<Dialog.Root open={open} onOpenChange={v => (open = v)}>
    <Dialog.Content>
        <div class="p-4">
            <h2 class="text-lg font-bold mb-4">Dobierz zespoły sędziowskie</h2>

            {#if cityQuery.isPending}
                <Spinner size="sm" />
            {:else if cityQuery.error}
                <div class="text-red-500 mb-4">{String(cityQuery.error)}</div>
            {:else}
                <table class="table-auto w-full border-collapse border border-gray-300">
                    <thead>
                    <tr>
                        <th class="konkurs border border-gray-300 px-4 py-2">Konkurs</th>
                        <th class="border border-gray-300 px-4 py-2">Sędziowie A</th>
                        <th class="border border-gray-300 px-4 py-2">Sędziowie B</th>
                        <th class="border border-gray-300 px-4 py-2">Sędziowie Stylu</th>
                    </tr>
                    </thead>
                    <tbody>
                    {#each cities as city}
                        <tr>
                            <td class="border border-gray-300 px-4 py-2" style="text-align: left">
                                <p>{city.name}</p>
                                <p class="px-0 text-xs text-muted-foreground">{city.level === "FINAL"
                                    ? "Finał Ogólnopolski"
                                    : "Finał Regionalny"
                                }</p>
                            </td>
                            <td class="border border-gray-300 px-4 py-2">
                                <input
                                        type="number"
                                        min="0"
                                        class="w-full border rounded px-2 py-1"
                                        value={judgeTeams.find(team => team.cityId === city.id)?.judgeCount.dtA ?? 1}
                                        oninput={(e) => updateJudgeTeam(city.id, 'dtA', e)}
                                />
                            </td>
                            <td class="border border-gray-300 px-4 py-2">
                                <input
                                        type="number"
                                        min="0"
                                        class="w-full border rounded px-2 py-1"
                                        value={judgeTeams.find(team => team.cityId === city.id)?.judgeCount.dtB ?? 1}
                                        oninput={(e) => updateJudgeTeam(city.id, 'dtB', e)}
                                />
                            </td>
                            <td class="border border-gray-300 px-4 py-2">
                                <input
                                        type="number"
                                        min="0"
                                        class="w-full border rounded px-2 py-1"
                                        value={judgeTeams.find(team => team.cityId === city.id)?.judgeCount.style ?? 1}
                                        oninput={(e) => updateJudgeTeam(city.id, 'style', e)}
                                />
                            </td>
                        </tr>
                    {/each}
                    </tbody>
                </table>
            {/if}

            <div class="mt-4 flex justify-end gap-4">
                <Button onclick={() => (open = false)} variant="secondary">Anuluj</Button>
                <Button onclick={saveJudgeTeams}>Zapisz</Button>
            </div>
        </div>
    </Dialog.Content>
</Dialog.Root>

<style lang="css">
    table {
        border-collapse: collapse;
        width: 100%;
    }

    th, td {
        text-align: center;
        padding: 8px;
    }

    th.konkurs {
        width: 40%;
    }
</style>