<script lang="ts">
    import type { User } from './userService';
    import type { TableSource } from '@skeletonlabs/skeleton';
    import { Table } from '@skeletonlabs/skeleton';
    import Dialog from '$lib/Dialog.svelte';
    import UserRolesDialog from './UserRolesDialog.svelte';

    export let data: { users: User[]; fetchError?: string };

    let userDialog: HTMLDialogElement;
    let selectedUser: User | null = null;

    function mapUsersToTable(): TableSource {
        return {
            head: ['Nazwa', 'Email', 'User ID', 'Role'],
            body: data.users.map((u) => [u.username, u.email, u.userId, (u.roles ?? []).join(', ')]),
            meta: data.users.map((u) => [u.id, u.username, u.email, u.userId, u.roles]),
            foot: [`<code class="code">${data.users.length}</code>`],
        };
    }

    function onUserSelected(event: CustomEvent<number[]>) {
        const id = event.detail?.[0];
        if (id != null) {
            selectedUser = data.users.find((u) => u.id === id) ?? null;
            if (selectedUser) userDialog.showModal();
        }
    }

    function onRolesSaved() {
        userDialog.close();
        selectedUser = null;
        refresh();
    }

    function onRolesCancelled() {
        userDialog.close();
        selectedUser = null;
    }

    async function refresh() {
        const { fetchUsers } = await import('./userService');
        data = await fetchUsers()
            .then((users) => ({ users }))
            .catch(() => ({ users: [], fetchError: 'Nie udało się pobrać użytkowników.' }));
    }
</script>

<h1 class="mb-6">Zarządzanie użytkownikami</h1>

{#if data.fetchError}
    <div class="text-error-500 mb-4">{data.fetchError}</div>
{/if}

{#if data.users.length === 0}
    <div>Brak użytkowników.</div>
{:else}
    <div class="card">
        <section class="p-4">
            <Table
                source={mapUsersToTable()}
                interactive="true"
                on:selected={onUserSelected}
            />
        </section>
    </div>
{/if}

<Dialog bind:dialog={userDialog}>
    {#if selectedUser}
        <UserRolesDialog user={selectedUser} onSave={onRolesSaved} onCancel={onRolesCancelled} />
    {/if}
</Dialog>
