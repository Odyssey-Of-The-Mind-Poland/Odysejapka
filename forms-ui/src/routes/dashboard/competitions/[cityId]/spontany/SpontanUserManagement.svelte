<script lang="ts">
    import * as Table from "$lib/components/ui/table/index.js";
    import * as Collapsible from "$lib/components/ui/collapsible/index.js";
    import {Button} from "$lib/components/ui/button/index.js";
    import IconTrash from "@tabler/icons-svelte/icons/trash";
    import IconChevronDown from "@tabler/icons-svelte/icons/chevron-down";
    import SpontanCredentialsButton from "./SpontanCredentialsButton.svelte";

    type SpontanUserInfo = {
        id: number;
        userId: number;
        name: string;
        email: string;
    };

    let {
        cityId,
        users,
        onCreateUser,
        onDeleteUser,
    }: {
        cityId: number;
        users: SpontanUserInfo[];
        onCreateUser: (username: string) => void;
        onDeleteUser: (userId: number) => void;
    } = $props();

    let newUsername = $state('');
    let usersOpen = $state(false);

    function handleCreate() {
        if (!newUsername.trim()) return;
        onCreateUser(newUsername.trim());
        newUsername = '';
    }
</script>

<Collapsible.Root bind:open={usersOpen}>
    <div class="rounded-xl border bg-card shadow-sm">
        <Collapsible.Trigger class="flex w-full items-center justify-between p-4 cursor-pointer hover:bg-muted/30 transition-colors rounded-xl">
            <h3 class="text-lg font-semibold">Użytkownicy spontan</h3>
            <IconChevronDown class="size-5 text-muted-foreground transition-transform duration-200 {usersOpen ? 'rotate-180' : ''}"/>
        </Collapsible.Trigger>
        <Collapsible.Content>
            <div class="px-4 pb-4 pt-0">
                <div class="flex gap-2 mb-4">
                    <input
                            type="text"
                            bind:value={newUsername}
                            placeholder="Nazwa użytkownika"
                            class="flex h-9 w-full max-w-xs rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                            onkeydown={(e) => { if (e.key === 'Enter') handleCreate(); }}
                    />
                    <Button size="sm" onclick={handleCreate} disabled={!newUsername.trim()}>
                        Utwórz
                    </Button>
                </div>

                {#if users.length > 0}
                    <div class="rounded-lg border overflow-hidden">
                        <Table.Root>
                            <Table.Header>
                                <Table.Row class="bg-muted/40 hover:bg-muted/40">
                                    <Table.Head class="font-semibold">Nazwa</Table.Head>
                                    <Table.Head class="font-semibold">Email</Table.Head>
                                    <Table.Head class="font-semibold w-[200px]">Akcje</Table.Head>
                                </Table.Row>
                            </Table.Header>
                            <Table.Body>
                                {#each users as user (user.id)}
                                    <Table.Row>
                                        <Table.Cell>{user.name}</Table.Cell>
                                        <Table.Cell>
                                            <code class="text-xs">{user.email}</code>
                                        </Table.Cell>
                                        <Table.Cell>
                                            <div class="flex items-center gap-2">
                                                <SpontanCredentialsButton cityId={cityId} userId={user.userId} userName={user.name}/>
                                                <Button variant="outline" size="sm" onclick={() => onDeleteUser(user.userId)}>
                                                    <IconTrash class="size-4"/>
                                                </Button>
                                            </div>
                                        </Table.Cell>
                                    </Table.Row>
                                {/each}
                            </Table.Body>
                        </Table.Root>
                    </div>
                {:else}
                    <p class="text-sm text-muted-foreground">Brak użytkowników spontan dla tego miasta.</p>
                {/if}
            </div>
        </Collapsible.Content>
    </div>
</Collapsible.Root>
