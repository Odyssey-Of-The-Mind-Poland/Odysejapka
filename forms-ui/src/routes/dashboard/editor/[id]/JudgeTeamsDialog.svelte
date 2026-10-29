<script lang="ts">
    import * as Dialog from '$lib/components/ui/dialog/index.js';
    import {Button} from '$lib/components/ui/button/index.js';
    import {Spinner} from '$lib/components/ui/spinner';
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import type {City, JudgeTeam} from "./types";
    import {toast} from "svelte-sonner";

    let {open = $bindable(), problem}: {
        open: boolean, problem: string
    } = $props();

    let cityQuery = createOdysejaQuery<City[]>({
        queryKey: ['city'],
        path: `/api/v1/city`,
        enabled: open
    });
    let judgeTeamQuery = $derived(createOdysejaQuery<JudgeTeam[]>({
        queryKey: ['judgeTeams', problem],
        path: `/api/v1/form/${problem}/judge-teams`,
    }));
    let cities = $derived(cityQuery.data);
    let judgeTeams = $state(judgeTeamQuery.data ?? []);

    let originalJudgeTeams: JudgeTeam[] = JSON.parse(JSON.stringify(judgeTeams));

    let isDirty = $derived.by(() => JSON.stringify(judgeTeams) !== JSON.stringify(originalJudgeTeams));

    $effect(() => {
        if (open) {
            cityQuery.refetch();
        }
    });

    function updateJudgeTeam(cityId: number, field: keyof JudgeTeam["judgeCount"], e: Event) {
        const teamIndex = judgeTeams.findIndex(team => team.cityId === cityId);
        judgeTeams[teamIndex].judgeCount[field] = Number((e.target as HTMLInputElement).value) ?? 1;
    }

    let judgeTeamSaveMutation = $derived(createPutMutation<JudgeTeam[]>({
        path: `/api/v1/form/${problem}/judge-teams`,
        queryKey: ['judgeTeams', problem],
        onSuccess: data => {
            toast.success("Zespół sędziowski zapisany pomyślnie");
        }
    }))

    function saveJudgeTeams() {
        judgeTeamSaveMutation.mutate(judgeTeams);
        open = false;
        originalJudgeTeams = JSON.parse(JSON.stringify(judgeTeams));
    }

    function cancelChanges() {
        open = false;
        judgeTeams = JSON.parse(JSON.stringify(originalJudgeTeams));
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
                <table class="table-auto w-full border-collapse border border-gray-300 px-4 py-2">
                    <thead>
                    <tr>
                        <th class="w-2/5 border px-2 py-1">Konkurs</th>
                        <th class="border px-2 py-1">Sędziowie A</th>
                        <th class="border px-2 py-1">Sędziowie B</th>
                        <th class="border px-2 py-1">Sędziowie Stylu</th>
                    </tr>
                    </thead>
                    <tbody>
                    {#each cities as city}
                        <tr>
                            <td class="w-2/5 border border-gray-300 px-4 py-2 text-left">
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
                <Button onclick={cancelChanges} variant="secondary">Anuluj</Button>
                <Button onclick={saveJudgeTeams} disabled={!isDirty}>Zapisz</Button>
            </div>
        </div>
    </Dialog.Content>
</Dialog.Root>