<script lang="ts">
    import type {Team} from "../types";
    import IconGripVertical from "@tabler/icons-svelte/icons/grip-vertical";
    import IconUsers from "@tabler/icons-svelte/icons/users";

    type Props = {
        teams: Team[];
        filter?: string;
    };

    let {teams, filter = ''}: Props = $props();

    let filteredTeams = $derived(
        filter.trim()
            ? teams.filter(t => t.teamName.toLowerCase().includes(filter.trim().toLowerCase()))
            : teams
    );
</script>

<div class="flex flex-col gap-1">
    {#if filteredTeams.length === 0}
        <div class="rounded-lg border border-dashed p-6 text-center">
            <IconUsers class="size-6 text-muted-foreground/40 mx-auto mb-2"/>
            <p class="text-sm text-muted-foreground">Brak druzyn</p>
        </div>
    {:else}
        {#each filteredTeams as team (team.id)}
            <div
                class="flex items-center gap-2 rounded-md border bg-card px-3 py-2 text-sm cursor-grab active:cursor-grabbing shadow-sm hover:shadow-md transition-shadow"
                draggable="true"
                ondragstart={(e) => {
                    e.dataTransfer?.setData('application/team-id', String(team.id));
                    e.dataTransfer?.setData('text/plain', team.teamName);
                    if (e.dataTransfer) e.dataTransfer.effectAllowed = 'move';
                }}
            >
                <IconGripVertical class="size-4 text-muted-foreground shrink-0"/>
                <div class="flex flex-col min-w-0">
                    <span class="font-medium truncate">{team.teamName}</span>
                    <span class="text-xs text-muted-foreground">
                        Problem {team.problem} | Wiek {team.age}
                    </span>
                </div>
            </div>
        {/each}
    {/if}
</div>
