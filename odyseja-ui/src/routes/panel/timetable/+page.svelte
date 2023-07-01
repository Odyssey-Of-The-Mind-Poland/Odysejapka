<script lang="ts">
    import {Table, tableMapperValues} from '@skeletonlabs/skeleton';
    import type {TableSource} from '@skeletonlabs/skeleton';
    import type {Timetable, Performance, PerformanceGroup} from '../types';
    import PerformanceComponent from "./Performance.svelte";
    import PerformanceGroupComponent from "./PerformanceGroupComponent.svelte";
    import cloneDeep from 'lodash/cloneDeep';
    import {fetchTimeTable} from "../apiService";
    import {compareGroups, comparePerformances, getGroupTitle} from "../types";
    import Filter from "./Filter.svelte";
    import Dialog from "$lib/Dialog.svelte";


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

    function onPerformanceSelected(meta: any): void {
        performance = data.timetable.flatMap(group => group.performances).find(performance => performance.id === meta.detail[0]);
        performanceDialog.showModal();
    }


    function onPerformanceGroupSelected(performanceGroup: PerformanceGroup): void {
        selectedPerformanceGroup = performanceGroup;
        performanceGroupDialog.showModal();
    }


    async function onPerformanceSaved() {
        let timetable = await fetchTimeTable();
        data = sortTimeTable(timetable);
        initialData = data
        performanceDialog.close();
        performanceGroupDialog.close();
    }

    function mapPerformancesToTable(performances: PerformanceComponent[]): TableSource {
        return {
            head: ['Zespół', 'Dzień występu', 'Godzina występu', 'Dzień spontanu', 'Spontan'],
            body: tableMapperValues(performances, ['team', 'performanceDay', 'performance', 'spontanDay', 'spontan']),
            meta: tableMapperValues(performances, ['id', 'city', 'team', 'performance', 'spontan']),
            foot: ['Total', '', `<code class="code">${performances.length}</code>`]
        };
    }

    async function addPerformance() {
      performance = {
        id: -1,
        city: "",
        team: "",
        problem: 0,
        age: 0,
        stage: 0,
        performance: "",
        spontan: "",
        part: 0,
        performanceDay: "",
        spontanDay: "",
        league: "",
      };
      performanceDialog.showModal();
    }
</script>

<div class="flex flex-wrap space-x-5">
  <div class="flex-grow flex-4 space-x-5">
    <Filter stages={stages} bind:selectedStages={selectedStages}
            ages={ages} bind:selectedAges={selectedAges}
            problems={problems} bind:selectedProblems={selectedProblems}/>
  </div>
    <button
            type="button"
            class="btn btn-xl variant-filled-primary h-full"
            on:click={addPerformance}>Dodaj występ
    </button>
</div>


{#each data.timetable as performanceGroup (performanceGroup.group)}
    <div class="card card-hover cursor-pointer mb-6"
         on:click={() => onPerformanceGroupSelected(performanceGroup)}>
        <header class="card-header">{getGroupTitle(performanceGroup.group)}</header>
        <section class="p-4">
            <Table source={mapPerformancesToTable(performanceGroup.performances)} on:selected={onPerformanceSelected}
                   interactive="true"/>
        </section>
    </div>
{/each}

<div id="overlay" class="fixed inset-0 bg-black bg-opacity-50 hidden"></div>

<Dialog bind:dialog={performanceDialog}>
    <PerformanceComponent performance={cloneDeep(performance)} onSave={onPerformanceSaved}/>
</Dialog>


<Dialog bind:dialog={performanceGroupDialog}>
    <PerformanceGroupComponent performanceGroup={cloneDeep(selectedPerformanceGroup)} onSave={onPerformanceSaved}/>
</Dialog>
