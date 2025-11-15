<script lang="ts">
    import {goto} from '$app/navigation';
    import {page} from '$app/state';   // reactive object

    import * as Drawer from '$lib/components/ui/drawer/index.js';
    import * as Table from '$lib/components/ui/table/index.js';

    let {children, data} = $props<{
        data: { users: User[]; fetchError?: string };
    }>();

    const routeId = $derived(page.params.id);

    const drawerOpen = $derived(Boolean(routeId));

    let users = $derived(data.users);

    function handleOpenChange(open: boolean) {
        if (!open) goto('/dashboard/users');
    }
</script>

<div class="flex flex-1 flex-col gap-8 p-4">
    {#if data.fetchError}
        <div class="text-red-500 mb-4">{data.fetchError}</div>
    {/if}

    {#if users.length === 0}
        <div>No users found.</div>
    {:else}
        <div class="rounded-md border overflow-x-auto">
            <Table.Root>
                <Table.Header>
                    <Table.Row>
                        <Table.Head>Name</Table.Head>
                        <Table.Head>Email</Table.Head>
                        <Table.Head>User&nbsp;ID</Table.Head>
                        <Table.Head>Roles</Table.Head>
                    </Table.Row>
                </Table.Header>

                <Table.Body>
                    {#each users as user (user.id)}
                        <Table.Row
                                class="cursor-pointer hover:bg-muted/50"
                                onclick={() => goto(`/dashboard/users/${user.id}`)}
                        >
                            <Table.Cell>{user.username}</Table.Cell>
                            <Table.Cell class="lowercase">{user.email}</Table.Cell>
                            <Table.Cell>{user.userId}</Table.Cell>
                            <Table.Cell>{user.roles.join(', ')}</Table.Cell>
                        </Table.Row>
                    {/each}
                </Table.Body>
            </Table.Root>
        </div>
    {/if}
</div>

<Drawer.Root
        onOpenChange={handleOpenChange}
        direction="right"
        open={drawerOpen}
>
    <Drawer.Content>
        {@render children()}
    </Drawer.Content>
</Drawer.Root>
