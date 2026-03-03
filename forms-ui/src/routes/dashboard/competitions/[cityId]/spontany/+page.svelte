<script lang="ts">
    import {createOdysejaQuery, createPostMutation, createPutMutation, createDelMutation} from "$lib/queries";
    import {setBreadcrumbs} from "$lib/breadcrumbs";
    import {page} from "$app/state";
    import {toast} from "svelte-sonner";
    import {goto} from "$app/navigation";
    import {currentUser} from "$lib/userStore";
    import SpontanUserManagement from "./SpontanUserManagement.svelte";
    import SpontanGroupsTable from "./SpontanGroupsTable.svelte";

    type City = { id: number; name: string; };

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

    type SpontanUserCredentials = {
        email: string;
        password: string;
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

    let spontansQuery = $derived(createOdysejaQuery<SpontanDefinition[]>({
        queryKey: ['spontans'],
        path: '/api/v1/spontan',
    }));

    let groupsQuery = $derived(createOdysejaQuery<SpontanGroupAssignment[]>({
        queryKey: ['spontanGroups'],
        path: `/api/v1/spontan/group/${cityId}`,
    }));

    let spontanUsersQuery = $derived(createOdysejaQuery<SpontanUserInfo[]>({
        queryKey: ['spontanUsers', String(cityId)],
        path: `/api/v1/spontan/user/${cityId}`,
        enabled: isAdmin,
    }));

    let spontanUsers = $derived(spontanUsersQuery.data ?? []);

    let createUserMutation = createPostMutation<SpontanUserCredentials, {
        body: { username: string };
    }>({
        path: () => `/api/v1/spontan/user/${cityId}`,
        queryKey: ['spontanUsers', String(cityId)],
        onSuccess: (data) => {
            toast.success(`Utworzono użytkownika: ${data.email}`);
        },
    });

    let deleteUserMutation = createDelMutation<void, { userId: number }>({
        path: (vars) => `/api/v1/spontan/user/${cityId}/${vars.userId}`,
        queryKey: ['spontanUsers', String(cityId)],
        onSuccess: () => toast.success('Użytkownik usunięty'),
    });

    let assignMutation = createPutMutation<SpontanGroupAssignment, {
        cityId: number;
        groupId: GroupId;
        body: { spontanDefinitionId: number | null };
    }>({
        path: (vars) => `/api/v1/spontan/group/${vars.cityId}/assign?problem=${vars.groupId.problem}&age=${vars.groupId.age}&league=${encodeURIComponent(vars.groupId.league)}`,
        queryKey: ['spontanGroups'],
        onSuccess: () => toast.success('Spontan przypisany'),
    });

    let judgesMutation = createPutMutation<SpontanGroupAssignment, {
        cityId: number;
        groupId: GroupId;
        body: { judgeCount: number };
    }>({
        path: (vars) => `/api/v1/spontan/group/${vars.cityId}/judges?problem=${vars.groupId.problem}&age=${vars.groupId.age}&league=${encodeURIComponent(vars.groupId.league)}`,
        queryKey: ['spontanGroups'],
        onSuccess: () => toast.success('Liczba sędziów zmieniona'),
    });

    let assignUserMutation = createPutMutation<void, {
        assignmentId: number;
        body: { spontanUserId: number | null };
    }>({
        path: (vars) => `/api/v1/spontan/group/${vars.assignmentId}/user`,
        queryKey: ['spontanGroups'],
        onSuccess: () => toast.success('Użytkownik przypisany do grupy'),
    });

    function handleCreateUser(username: string) {
        createUserMutation.mutate({body: {username}});
    }

    function handleDeleteUser(userId: number) {
        deleteUserMutation.mutate({userId});
    }

    function handleAssign(group: SpontanGroupAssignment, spontanId: string) {
        assignMutation.mutate({
            cityId,
            groupId: group.groupId,
            body: {spontanDefinitionId: spontanId === '__none__' ? null : Number(spontanId)},
        });
    }

    function handleJudgeCount(group: SpontanGroupAssignment, count: string) {
        judgesMutation.mutate({
            cityId,
            groupId: group.groupId,
            body: {judgeCount: Number(count)},
        });
    }

    function handleAssignUser(group: SpontanGroupAssignment, value: string) {
        if (!group.id) return;
        assignUserMutation.mutate({
            assignmentId: group.id,
            body: {spontanUserId: value === '__none__' ? null : Number(value)},
        });
    }

    function handleNavigateToGroup(group: SpontanGroupAssignment) {
        if (group.spontanDefinitionId) {
            const g = group.groupId;
            goto(`/dashboard/competitions/${cityId}/spontany/${g.problem}/${g.age}/${g.league || '_'}`);
        }
    }

    $effect(() => {
        setBreadcrumbs([
            {name: 'Konkursy', href: '/dashboard/competitions'},
            {name: cityName, href: `/dashboard/competitions/${cityId}/dt`},
        ]);
    });
</script>

<div class="flex flex-col gap-6">
    {#if isAdmin}
        <SpontanUserManagement
                cityId={cityId}
                users={spontanUsers}
                onCreateUser={handleCreateUser}
                onDeleteUser={handleDeleteUser}
        />
    {/if}

    <SpontanGroupsTable
            groups={groupsQuery.data ?? []}
            isPending={groupsQuery.isPending}
            error={groupsQuery.error}
            spontanDefinitions={spontansQuery.data ?? []}
            {spontanUsers}
            {isAdmin}
            onAssignSpontan={handleAssign}
            onSetJudgeCount={handleJudgeCount}
            onAssignUser={handleAssignUser}
            onNavigateToGroup={handleNavigateToGroup}
    />
</div>
