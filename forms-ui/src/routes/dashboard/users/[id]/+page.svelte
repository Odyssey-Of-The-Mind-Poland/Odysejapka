<script lang="ts">
    import {goto, invalidateAll} from '$app/navigation';
    import * as Drawer from '$lib/components/ui/drawer/index.js';
    import {Button, buttonVariants} from '$lib/components/ui/button/index.js';
    import {apiFetch} from '$lib/api';
    import * as Select from '$lib/components/ui/select/index.js';
    import type {PageData} from './$types';

    let {data}: { data: PageData } = $props();

    let editedRoles = $state<string[]>([...(data.user?.roles ?? [])]);
    let selectedRole = $state<string | undefined>(undefined);

    let remainingRoles = $derived((data.roles as string[]).filter(r => !editedRoles.includes(r)));

    $effect(() => {
        if (selectedRole) {
            if (!editedRoles.includes(selectedRole)) {
                editedRoles = [...editedRoles, selectedRole];
            }
            selectedRole = undefined;
        }
        console.log(JSON.stringify(data))
    });

    function removeRole(role: string) {
        editedRoles = editedRoles.filter((r) => r !== role);
    }

    async function saveRoles() {
        if (!data.user) return;
        const res = await apiFetch(`/api/v1/users/${data.user.id}/roles`, {
            method: 'POST',
            body: JSON.stringify(editedRoles)
        });

        await invalidateAll();
        await goto('/dashboard/users');

        if (!res) alert('Failed to update roles.');
    }
</script>

<div class="mx-auto w-full max-w-md">
    <Drawer.Header>
        <Drawer.Title>Edit User Roles</Drawer.Title>
        <Drawer.Description>
            Manage roles for {data.user?.username}
        </Drawer.Description>
    </Drawer.Header>

    <div class="p-4 space-y-4">
        <div>
            <div class="font-medium text-sm">Email:</div>
            <div class="text-muted-foreground text-sm">{data.user?.email}</div>
        </div>

        <div>
            <div class="font-medium text-sm mb-1">Roles:</div>
            <div class="flex flex-wrap gap-2">
                {#each editedRoles as role}
					<span class="bg-muted px-2 py-1 rounded text-xs flex items-center gap-1">
						{role}
                        <button
                                onclick={() => removeRole(role)}
                                class="text-red-500 font-bold text-xs"
                        >
							✕
						</button>
					</span>
                {/each}
            </div>
        </div>

        <Select.Root bind:value={selectedRole} type="single">
            <Select.Trigger class="w-full">
                Select role
            </Select.Trigger>

            <Select.Content>
                <Select.Group>
                    <Select.Label>Roles</Select.Label>

                    {#each remainingRoles as role}
                        <Select.Item value={role} label={role}>
                            {role}
                        </Select.Item>
                    {/each}
                </Select.Group>
            </Select.Content>
        </Select.Root>
    </div>

    <Drawer.Footer>
        <Button onclick={saveRoles}>Save</Button>
        <Drawer.Close class={buttonVariants({ variant: 'outline' })}>
            Cancel
        </Drawer.Close>
    </Drawer.Footer>
</div>
