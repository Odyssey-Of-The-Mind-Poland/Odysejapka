<script lang="ts">
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery, createPutMutation} from "$lib/queries";
    import {Button} from "$lib/components/ui/button/index.js";
    import {Input} from "$lib/components/ui/input/index.js";
    import * as Card from "$lib/components/ui/card/index.js";
    import IconAlertTriangle from "@tabler/icons-svelte/icons/alert-triangle";
    import IconDeviceFloppy from "@tabler/icons-svelte/icons/device-floppy";
    import {toast} from "svelte-sonner";

    type BreakingChange = {
        version: string;
    };

    let breakingQuery = createOdysejaQuery<BreakingChange>({
        queryKey: ['lappkaBreakingChange'],
        path: '/api/breaking/change',
    });

    let editVersion = $state('');
    let isEditing = $state(false);

    function startEdit() {
        editVersion = breakingQuery.data?.version ?? '';
        isEditing = true;
    }

    function cancelEdit() {
        isEditing = false;
    }

    let updateMutation = createPutMutation<void, { version: string }>({
        path: '/api/breaking/change',
        queryKey: ['lappkaBreakingChange'],
        onSuccess: () => {
            isEditing = false;
            toast.success('Wersja zapisana');
        },
    });

    function handleSave() {
        if (!editVersion.trim()) return;
        updateMutation.mutate({version: editVersion.trim()});
    }

    let hasChanges = $derived(isEditing && editVersion !== (breakingQuery.data?.version ?? ''));
</script>

<div class="flex flex-col gap-6">
    <div class="flex items-center gap-3">
        <div class="flex items-center justify-center size-10 rounded-lg bg-orange-100">
            <IconAlertTriangle class="size-5 text-orange-600"/>
        </div>
        <div>
            <h1 class="text-xl font-semibold tracking-tight">Breaking Change</h1>
            <p class="text-sm text-muted-foreground">
                Wymuś aktualizację aplikacji mobilnej zmieniając wersję
            </p>
        </div>
    </div>

    {#if breakingQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie...</p>
        </div>
    {:else if breakingQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd ładowania</p>
        </div>
    {:else}
        <Card.Root class="max-w-md">
            <Card.Header>
                <Card.Title class="text-base">Wersja breaking change</Card.Title>
                <Card.Description>
                    Użytkownicy z wersją niższą niż podana zobaczą komunikat o konieczności aktualizacji
                </Card.Description>
            </Card.Header>
            <Card.Content>
                {#if isEditing}
                    <div class="flex flex-col gap-3">
                        <Input
                                bind:value={editVersion}
                                placeholder="np. 1.2.0"
                                class="font-mono"
                        />
                        <div class="flex gap-2">
                            <Button
                                    size="sm"
                                    onclick={handleSave}
                                    disabled={!hasChanges || updateMutation.isPending}
                            >
                                <IconDeviceFloppy class="size-4 mr-1"/>
                                Zapisz
                            </Button>
                            <Button variant="outline" size="sm" onclick={cancelEdit}>
                                Anuluj
                            </Button>
                        </div>
                    </div>
                {:else}
                    <div class="flex items-center justify-between">
                        <code class="text-lg font-mono bg-muted px-3 py-1 rounded">
                            {breakingQuery.data?.version ?? '0.0.0'}
                        </code>
                        <Button variant="outline" size="sm" onclick={startEdit}>
                            Zmień
                        </Button>
                    </div>
                {/if}
            </Card.Content>
        </Card.Root>
    {/if}
</div>
