<script lang="ts">
  import { Table, tableMapperValues } from '@skeletonlabs/skeleton';
  import type { TableSource } from '@skeletonlabs/skeleton';
  import type { Group, Timetable } from 'types';

  export let data: Timetable;
  console.log('data', data)

  function onSelected(meta: any): void {
    console.log('on:selected', meta);
  }

  function mapPerformancesToTable(performances: Performance[]): TableSource {
    return {
      head: ['Id', 'City', 'Team', 'Performance', 'Spontan'],
      body: tableMapperValues(performances, ['id', 'city', 'team', 'performance', 'spontan']),
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
      <Table source={mapPerformancesToTable(performanceGroup.performances)} on:selected={onSelected} interactive="true"/>
    </section>
  </div>
{/each}