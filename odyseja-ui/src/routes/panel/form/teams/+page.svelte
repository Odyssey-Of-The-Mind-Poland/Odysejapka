<script lang="ts">
    import {Table, tableMapperValues} from '@skeletonlabs/skeleton';
    import type {TableSource} from '@skeletonlabs/skeleton';
    import type {Timetable, Performance, PerformanceGroup, City} from '$lib/types';
    import cloneDeep from 'lodash/cloneDeep';
    import PerformanceComponent from "../../timetable/Performance.svelte";
    import {compareGroups, comparePerformances, getGroupTitle} from "$lib/types";
    import Dialog from "$lib/Dialog.svelte";
    import {city} from "$lib/cityStore";
    import Filter from "../../timetable/Filter.svelte";
    import {fetchTimeTable} from "../../timetable/performanceService";
    import {goto} from "$app/navigation";


    export let data: Timetable;
    let initialData = cloneDeep(data);

    let performanceDialog: HTMLDialogElement;
    let performance: Performance | undefined = {} as Performance;

    let performanceGroupDialog: HTMLDialogElement;
    let selectedPerformanceGroup: PerformanceGroup | undefined = data.timetable[0] as PerformanceGroup;

    let stages = [1, 2, 3, 4, 5, 6, 7]
    let selectedStages = [1, 2, 3, 4, 5, 6, 7]

    let ages = [0, 1, 2, 3, 4, 5]
    let selectedAges = [0, 1, 2, 3, 4, 5]

    let problems = [0, 1, 2, 3, 4, 5]
    let selectedProblems = [0, 1, 2, 3, 4, 5]

    $: {
        if (data) {
            let filtered = initialData.timetable.filter((item) => {
                return selectedStages.includes(item.group.stage) &&
                    selectedAges.includes(item.group.age) &&
                    selectedProblems.includes(item.group.problem);
            });

            console.log('filtered', filtered)

            data = sortTimeTable({timetable: filtered} as Timetable);
        }
    }


    function sortTimeTable(timeTable: Timetable): Timetable {
        timeTable.timetable.sort((a, b) => compareGroups(a.group, b.group));
        timeTable.timetable.forEach(performanceGroup => {
            performanceGroup.performances.sort((a, b) => comparePerformances(a, b));
        });
        return cloneDeep(timeTable) as Timetable;
    }

    async function onPerformanceSelected(meta: any) {
        await goto(`/panel/form/teams/${meta.detail[0]}`);
    }

    city.subscribe(async curr => {
        let timetable = await fetchTimeTable();
        data = sortTimeTable(timetable);
        initialData = data
    });

    function mapPerformancesToTable(performances: PerformanceComponent[]): TableSource {
        return {
            head: ['Zespół', 'Dzień występu', 'Godzina występu', 'Dzień spontanu', 'Spontan'],
            body: tableMapperValues(performances, ['team', 'performanceDay', 'performance', 'spontanDay', 'spontan']),
            meta: tableMapperValues(performances, ['id', 'city', 'team', 'performance', 'spontan']),
            foot: ['Total', '', `<code class="code">${performances.length}</code>`]
        };
    }

</script>

<div class="flex flex-wrap space-x-5">
  <div class="flex-grow flex-4 space-x-5">
    <Filter stages={stages} bind:selectedStages={selectedStages}
            ages={ages} bind:selectedAges={selectedAges}
            problems={problems} bind:selectedProblems={selectedProblems}/>
  </div>
</div>

{#if (data.timetable.length === 0)}
    <div class="text-center text-2xl">Brak występów</div>
{:else}
    {#each data.timetable as performanceGroup (performanceGroup.group)}
        <div class="card card-hover cursor-pointer mb-6">
            <header class="card-header">{getGroupTitle(performanceGroup.group)}</header>
            <section class="p-4">
                <Table source={mapPerformancesToTable(performanceGroup.performances)} on:selected={onPerformanceSelected}
                       interactive="true"/>
            </section>
        </div>
    {/each}

    <div id="overlay" class="fixed inset-0 bg-black bg-opacity-50 hidden"></div>
{/if}
