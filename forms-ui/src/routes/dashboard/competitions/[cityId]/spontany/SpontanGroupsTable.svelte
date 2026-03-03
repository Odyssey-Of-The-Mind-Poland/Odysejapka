<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import * as Table from "$lib/components/ui/table/index.js";
    import * as Select from "$lib/components/ui/select/index.js";
    import {Badge} from "$lib/components/ui/badge/index.js";
    import IconSparkles from "@tabler/icons-svelte/icons/sparkles";

    type SpontanDefinition = {
        id: number;
        name: string;
        type: 'VERBAL' | 'MANUAL';
    };

    type GroupId = {
        problem: number;
        age: number;
        league: string;
    };

    type SpontanGroupAssignment = {
        id: number | null;
        cityId: number;
        groupId: GroupId;
        spontanDefinitionId: number | null;
        spontanDefinitionName: string | null;
        spontanType: string | null;
        judgeCount: number;
        spontanUserId: number | null;
        spontanUserName: string | null;
    };

    type SpontanUserInfo = {
        id: number;
        userId: number;
        name: string;
        email: string;
    };

    let {
        groups,
        isPending,
        error,
        spontanDefinitions,
        spontanUsers,
        isAdmin,
        onAssignSpontan,
        onSetJudgeCount,
        onAssignUser,
        onNavigateToGroup,
    }: {
        groups: SpontanGroupAssignment[];
        isPending: boolean;
        error: Error | null;
        spontanDefinitions: SpontanDefinition[];
        spontanUsers: SpontanUserInfo[];
        isAdmin: boolean;
        onAssignSpontan: (group: SpontanGroupAssignment, spontanId: string) => void;
        onSetJudgeCount: (group: SpontanGroupAssignment, count: string) => void;
        onAssignUser: (group: SpontanGroupAssignment, value: string) => void;
        onNavigateToGroup: (group: SpontanGroupAssignment) => void;
    } = $props();

    function groupKey(g: SpontanGroupAssignment, i: number): string {
        return `${g.groupId.problem}-${g.groupId.age}-${g.groupId.league}-${i}`;
    }
</script>

{#if isPending}
    <div class="flex flex-col items-center justify-center py-16 gap-3">
        <Spinner size="sm"/>
        <p class="text-sm text-muted-foreground">Ładowanie grup...</p>
    </div>
{:else if error}
    <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
        <p class="font-medium text-destructive">Błąd podczas ładowania</p>
        <p class="text-sm text-muted-foreground mt-1">{String(error)}</p>
    </div>
{:else if groups.length > 0}
    <div class="rounded-xl border bg-card shadow-sm overflow-hidden">
        <Table.Root>
            <Table.Header>
                <Table.Row class="bg-muted/40 hover:bg-muted/40">
                    <Table.Head class="font-semibold">Grupa</Table.Head>
                    <Table.Head class="font-semibold">Przypisany spontan</Table.Head>
                    <Table.Head class="font-semibold">Liczba sędziów</Table.Head>
                    <Table.Head class="font-semibold">Typ</Table.Head>
                    <Table.Head class="font-semibold">Użytkownik</Table.Head>
                </Table.Row>
            </Table.Header>
            <Table.Body>
                {#each groups as group, i (groupKey(group, i))}
                    <Table.Row
                            class={group.spontanDefinitionId ? 'cursor-pointer transition-colors hover:bg-muted/50' : ''}
                            onclick={() => onNavigateToGroup(group)}
                    >
                        <Table.Cell>
                            <div class="flex items-center gap-2">
                                <Badge variant="outline" class="font-mono tabular-nums">Problem {group.groupId.problem}</Badge>
                                <Badge variant="secondary" class="font-mono tabular-nums">Grupa wiekowa {group.groupId.age}</Badge>
                                {#if group.groupId.league}
                                    <Badge variant="outline">{group.groupId.league}</Badge>
                                {/if}
                            </div>
                        </Table.Cell>
                        <Table.Cell>
                            <!-- svelte-ignore a11y_click_events_have_key_events -->
                            <!-- svelte-ignore a11y_no_static_element_interactions -->
                            <div onclick={(e) => e.stopPropagation()}>
                                {#if isAdmin}
                                    <Select.Root
                                            type="single"
                                            value={group.spontanDefinitionId ? String(group.spontanDefinitionId) : undefined}
                                            onValueChange={(v) => onAssignSpontan(group, v === '__none__' ? '__none__' : v ?? '__none__')}
                                    >
                                        <Select.Trigger class="w-[220px]">
                                            {group.spontanDefinitionName ?? 'Brak'}
                                        </Select.Trigger>
                                        <Select.Content>
                                            <Select.Item value="__none__">Brak</Select.Item>
                                            {#each spontanDefinitions as spontan (spontan.id)}
                                                <Select.Item value={String(spontan.id)}>
                                                    {spontan.name} ({spontan.type === 'VERBAL' ? 'Słowny' : 'Manualny'})
                                                </Select.Item>
                                            {/each}
                                        </Select.Content>
                                    </Select.Root>
                                {:else}
                                    <span class="text-sm">{group.spontanDefinitionName ?? 'Brak'}</span>
                                {/if}
                            </div>
                        </Table.Cell>
                        <Table.Cell>
                            <!-- svelte-ignore a11y_click_events_have_key_events -->
                            <!-- svelte-ignore a11y_no_static_element_interactions -->
                            <div onclick={(e) => e.stopPropagation()}>
                                {#if isAdmin}
                                    <Select.Root
                                            type="single"
                                            value={String(group.judgeCount)}
                                            onValueChange={(v) => { if (v) onSetJudgeCount(group, v); }}
                                    >
                                        <Select.Trigger class="w-[80px]">
                                            {group.judgeCount}
                                        </Select.Trigger>
                                        <Select.Content>
                                            <Select.Item value="1">1</Select.Item>
                                            <Select.Item value="2">2</Select.Item>
                                            <Select.Item value="3">3</Select.Item>
                                            <Select.Item value="4">4</Select.Item>
                                        </Select.Content>
                                    </Select.Root>
                                {:else}
                                    <span class="text-sm">{group.judgeCount}</span>
                                {/if}
                            </div>
                        </Table.Cell>
                        <Table.Cell>
                            {#if group.spontanDefinitionId}
                                <Badge variant="default" class="text-xs">
                                    {group.spontanType === 'VERBAL' ? 'Słowny' : 'Manualny'}
                                </Badge>
                            {/if}
                        </Table.Cell>
                        <Table.Cell>
                            <!-- svelte-ignore a11y_click_events_have_key_events -->
                            <!-- svelte-ignore a11y_no_static_element_interactions -->
                            <div onclick={(e) => e.stopPropagation()}>
                                {#if isAdmin && group.id}
                                    <Select.Root
                                            type="single"
                                            value={group.spontanUserId ? String(group.spontanUserId) : undefined}
                                            onValueChange={(v) => onAssignUser(group, v === '__none__' ? '__none__' : v ?? '__none__')}
                                    >
                                        <Select.Trigger class="w-[180px]">
                                            {group.spontanUserName ?? 'Brak'}
                                        </Select.Trigger>
                                        <Select.Content>
                                            <Select.Item value="__none__">Brak</Select.Item>
                                            {#each spontanUsers as user (user.id)}
                                                <Select.Item value={String(user.id)}>
                                                    {user.name}
                                                </Select.Item>
                                            {/each}
                                        </Select.Content>
                                    </Select.Root>
                                {:else}
                                    <span class="text-sm">{group.spontanUserName ?? '—'}</span>
                                {/if}
                            </div>
                        </Table.Cell>
                    </Table.Row>
                {/each}
            </Table.Body>
        </Table.Root>
    </div>
{:else}
    <div class="rounded-lg border border-dashed p-12 text-center">
        <IconSparkles class="size-10 text-muted-foreground/40 mx-auto mb-3"/>
        <p class="text-muted-foreground font-medium">Brak grup</p>
        <p class="text-sm text-muted-foreground/70 mt-1">W tym mieście nie ma jeszcze drużyn.</p>
    </div>
{/if}
