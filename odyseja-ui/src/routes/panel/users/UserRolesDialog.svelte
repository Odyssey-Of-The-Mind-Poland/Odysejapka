<script lang="ts">
    import type { User } from './userService';
    import { fetchRoles, updateUserRoles } from './userService';

    export let user: User;
    export let onSave: () => void;
    export let onCancel: () => void;

    let editedRoles: string[] = [];
    $: if (user) editedRoles = [...(user.roles ?? [])];

    let allRoles: string[] = [];
    let selectedRole = '';

    $: remainingRoles = allRoles.filter((r) => !editedRoles.includes(r));

    async function loadRoles() {
        allRoles = await fetchRoles();
    }

    loadRoles();

    function removeRole(role: string) {
        editedRoles = editedRoles.filter((r) => r !== role);
    }

    function addRole(role: string) {
        if (role && !editedRoles.includes(role)) {
            editedRoles = [...editedRoles, role];
            selectedRole = '';
        }
    }

    async function saveRoles() {
        await updateUserRoles(user.id, editedRoles);
        onSave();
    }
</script>

<div class="space-y-4">
    <h2 class="text-xl font-semibold">Edytuj role użytkownika</h2>
    <p class="text-muted">{user.username}</p>

    <div>
        <label class="label">Email:</label>
        <div class="text-muted-foreground text-sm">{user.email}</div>
    </div>

    <div>
        <label class="label">Role:</label>
        <div class="flex flex-wrap gap-2 mt-1">
            {#each editedRoles as role}
                <span class="badge variant-filled-secondary flex items-center gap-1">
                    {role}
                    <button
                        type="button"
                        class="text-error-500 font-bold text-xs hover:opacity-80"
                        on:click={() => removeRole(role)}
                        aria-label="Usuń rolę"
                    >
                        ✕
                    </button>
                </span>
            {/each}
        </div>
    </div>

    <div>
        <label class="label">Dodaj rolę:</label>
        <select bind:value={selectedRole} class="select w-full" on:change={() => { addRole(selectedRole); }}>
            <option value="">-- Wybierz rolę --</option>
            {#each remainingRoles as role}
                <option value={role}>{role}</option>
            {/each}
        </select>
    </div>

    <div class="flex gap-2 mt-4">
        <button type="button" class="btn variant-filled-primary" on:click={saveRoles}>
            Zapisz
        </button>
        <button type="button" class="btn variant-filled-secondary" on:click={onCancel}>
            Anuluj
        </button>
    </div>
</div>
