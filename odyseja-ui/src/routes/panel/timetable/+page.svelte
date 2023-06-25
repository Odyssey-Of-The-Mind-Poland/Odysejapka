<script lang="ts">
    import {Table, tableMapperValues} from '@skeletonlabs/skeleton';
    import type {TableSource} from '@skeletonlabs/skeleton';
    import type {Group, Timetable, Performance} from '../types';
    import PerformanceComponent from "./Performance.svelte";
    import cloneDeep from 'lodash/cloneDeep';
    import {fetchTimeTable} from "../apiService";
    import {compareGroups, comparePerformances} from "../types";

    export let data: Timetable;

    let performanceDialog: HTMLDialogElement;
    let performance: Performance | undefined = {} as Performance;

    function sortTimeTable(timeTable: Timetable): Timetable {
        timeTable.timetable.sort((a, b) => compareGroups(a.group, b.group));
        timeTable.timetable.forEach(performanceGroup => {
            performanceGroup.performances.sort((a, b) => comparePerformances(a, b));
        });
        return cloneDeep(timeTable) as Timetable;
    }

    function onSelected(meta: any): void {
        console.log('on:selected', meta);
        performance = data.timetable.flatMap(group => group.performances).find(performance => performance.id === meta.detail[0]);
        performanceDialog.showModal();
    }

    async function onPerformanceSaved() {
        let timetable = await fetchTimeTable();
        data = sortTimeTable(timetable);
        performanceDialog.close();
    }

    function mapPerformancesToTable(performances: PerformanceComponent[]): TableSource {
        return {
            head: ['Zespół', 'Dzień występu', 'Godzina występu', 'Dzień spontanu', 'Spontan'],
            body: tableMapperValues(performances, ['team', 'performanceDay', 'performance', 'spontanDay', 'spontan']),
            meta: tableMapperValues(performances, ['id', 'city', 'team', 'performance', 'spontan']),
            foot: ['Total', '', `<code class="code">${performances.length}</code>`]
        };
    }

    function getGroupTitle(group: Group): string {
        let name = `Scena: ${group.stage} • Problem ${group.problem} • Gr. wiekowa ${group.age}`;
        if (group.part) {
            name = `${name} • Część ${group.part}`;
        }

        if (group.league) {
            name = `${name} • Liga ${group.league}`;
        }
        return name;
    }
</script>

{#each data.timetable as performanceGroup (performanceGroup.group)}
    <div class="card card-hover cursor-pointer mb-6" on:click={() => onSelected(performanceGroup.group)}>
        <header class="card-header">{getGroupTitle(performanceGroup.group)}</header>
        <section class="p-4">
            <Table source={mapPerformancesToTable(performanceGroup.performances)} on:selected={onSelected}
                   interactive="true"/>
        </section>
    </div>
{/each}

<div id="overlay" class="fixed inset-0 bg-black bg-opacity-50 hidden"></div>

<dialog bind:this={performanceDialog} class="card p-10 w-1/2">
    <button class="absolute top-0 right-0 m-2" type="button" on:click={() => performanceDialog.close()}>
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" class="h-6 w-6">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
    </button>

    <PerformanceComponent performance={cloneDeep(performance)} onSave={onPerformanceSaved}/>
</dialog>


