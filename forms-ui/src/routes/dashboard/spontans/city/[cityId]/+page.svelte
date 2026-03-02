<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import * as Table from "$lib/components/ui/table/index.js";
    import * as Select from "$lib/components/ui/select/index.js";
    import {Badge} from "$lib/components/ui/badge/index.js";
    import {onMount} from "svelte";
    import {page} from "$app/state";
    import IconSparkles from "@tabler/icons-svelte/icons/sparkles";
    import {toast} from "svelte-sonner";
    import {goto} from "$app/navigation";
    import {currentUser} from "$lib/userStore";

    type City = {
        id: number;
        name: string;
    };

    type SpontanDefinition = {
        id: number;
        name: string;
        type: 'VERBAL' | 'MANUAL';
    };

    type SpontanGroupAssignment = {
        id: number | null;
        cityId: number;
        problem: number;
        age: number;
        league: string | null;
        spontanDefinitionId: number | null;
        spontanDefinitionName: string | null;
        spontanType: string | null;
        judgeCount: number;
    };

    let cityId = $derived(Number(page.params.cityId));
    let isAdmin = $derived($currentUser?.roles.includes('ADMINISTRATOR') ?? false);

    let citiesQuery = createOdysejaQuery<City[]>({
        queryKey: ['dashboardCities'],
        path: '/api/v1/dashboard/cities',
    });

    let cityName = $derived.by(() => {
        if (!citiesQuery.data) return '...';
        return citiesQuery.data.find((c: City) => c.id === cityId)?.name ?? '...';
    });

    let spontansQuery = createOdysejaQuery<SpontanDefinition[]>({
        queryKey: ['spontans'],
        path: '/api/v1/spontan',
    });

    let groupsQuery = createOdysejaQuery<SpontanGroupAssignment[]>({
        queryKey: ['spontanGroups', String(page.params.cityId)],
        path: `/api/v1/spontan/group/${page.params.cityId}`,
    });

    let groups = $derived(groupsQuery.data ?? []);

    let assignMutation = createPutMutation<SpontanGroupAssignment, {
        cityId: number;
        problem: number;
        age: number;
        league: string | null;
        body: { spontanDefinitionId: number | null };
    }>({
        path: (vars) => `/api/v1/spontan/group/${vars.cityId}/assign?problem=${vars.problem}&age=${vars.age}${vars.league ? `&league=${encodeURIComponent(vars.league)}` : '&league='}`,
        queryKey: ['spontanGroups'],
        onSuccess: () => toast.success('Spontan przypisany'),
    });

    let judgesMutation = createPutMutation<SpontanGroupAssignment, {
        cityId: number;
        problem: number;
        age: number;
        league: string | null;
        body: { judgeCount: number };
    }>({
        path: (vars) => `/api/v1/spontan/group/${vars.cityId}/judges?problem=${vars.problem}&age=${vars.age}${vars.league ? `&league=${encodeURIComponent(vars.league)}` : '&league='}`,
        queryKey: ['spontanGroups'],
        onSuccess: () => toast.success('Liczba sędziów zmieniona'),
    });

    function handleAssign(group: SpontanGroupAssignment, spontanId: string) {
        assignMutation.mutate({
            cityId,
            problem: group.problem,
            age: group.age,
            league: group.league,
            body: {spontanDefinitionId: spontanId === '__none__' ? null : Number(spontanId)},
        });
    }

    function handleJudgeCount(group: SpontanGroupAssignment, count: string) {
        judgesMutation.mutate({
            cityId,
            problem: group.problem,
            age: group.age,
            league: group.league,
            body: {judgeCount: Number(count)},
        });
    }

    function groupKey(g: SpontanGroupAssignment): string {
        return `${g.problem}-${g.age}-${g.league ?? ''}`;
    }

    function navigateToGroup(group: SpontanGroupAssignment) {
        if (group.spontanDefinitionId) {
            goto(`/dashboard/spontans/city/${cityId}/${group.problem}/${group.age}/${group.league ?? ''}`);
        }
    }

    onMount(() => {
        setBreadcrumbs([
            {name: 'Spontany', href: '/dashboard/spontans'},
            {name: cityName, href: `/dashboard/spontans/city/${cityId}`},
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    <div class="flex flex-col gap-1">
        <div class="flex items-center gap-3">
            <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
                <IconSparkles class="size-5 text-primary"/>
            </div>
            <div>
                <h1 class="text-2xl font-semibold tracking-tight">Spontany - {cityName}</h1>
                <p class="text-sm text-muted-foreground">Przypisz spontany do grup</p>
            </div>
        </div>
    </div>

    {#if groupsQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie grup...</p>
        </div>
    {:else if groupsQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd podczas ładowania</p>
            <p class="text-sm text-muted-foreground mt-1">{String(groupsQuery.error)}</p>
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
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {#each groups as group (groupKey(group))}
                        <Table.Row
                                class={group.spontanDefinitionId ? 'cursor-pointer transition-colors hover:bg-muted/50' : ''}
                                onclick={() => navigateToGroup(group)}
                        >
                            <Table.Cell>
                                <div class="flex items-center gap-2">
                                    <Badge variant="outline" class="font-mono tabular-nums">Problem {group.problem}</Badge>
                                    <Badge variant="secondary" class="font-mono tabular-nums">Grupa wiekowa {group.age}</Badge>
                                    {#if group.league}
                                        <Badge variant="outline">{group.league}</Badge>
                                    {/if}
                                </div>
                            </Table.Cell>
                            <Table.Cell>
                                <!-- svelte-ignore a11y_click_events_have_key_events -->
                                <!-- svelte-ignore a11y_no_static_element_interactions -->
                                <div onclick={(e: MouseEvent) => e.stopPropagation()}>
                                    {#if isAdmin}
                                        <Select.Root
                                                type="single"
                                                value={group.spontanDefinitionId ? String(group.spontanDefinitionId) : '__none__'}
                                                onValueChange={(v) => { if (v) handleAssign(group, v); }}
                                        >
                                            <Select.Trigger class="w-[220px]">
                                                {group.spontanDefinitionName ?? 'Brak'}
                                            </Select.Trigger>
                                            <Select.Content>
                                                <Select.Item value="__none__">Brak</Select.Item>
                                                {#if spontansQuery.data}
                                                    {#each spontansQuery.data as spontan (spontan.id)}
                                                        <Select.Item value={String(spontan.id)}>
                                                            {spontan.name} ({spontan.type === 'VERBAL' ? 'Słowny' : 'Manualny'})
                                                        </Select.Item>
                                                    {/each}
                                                {/if}
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
                                <div onclick={(e: MouseEvent) => e.stopPropagation()}>
                                    {#if isAdmin}
                                        <Select.Root
                                                type="single"
                                                value={String(group.judgeCount)}
                                                onValueChange={(v) => { if (v) handleJudgeCount(group, v); }}
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
</div>
