<script lang="ts">
    import {page} from "$app/state";
    import {Spinner} from "$lib/components/ui/spinner";
    import {createOdysejaQuery, createDelMutation} from "$lib/queries";
    import {Button} from "$lib/components/ui/button/index.js";
    import IconHeart from "@tabler/icons-svelte/icons/heart";
    import IconTrash from "@tabler/icons-svelte/icons/trash";
    import IconUpload from "@tabler/icons-svelte/icons/upload";
    import IconPlus from "@tabler/icons-svelte/icons/plus";
    import {toast} from "svelte-sonner";
    import {getQueryClientContext} from "@tanstack/svelte-query";

    type Sponsor = {
        id: number;
        row: number;
        column: number;
    };

    let cityId = $derived(Number(page.params.cityId));
    let uploading = $state(false);

    const queryClient = getQueryClientContext();

    let sponsorsQuery = $derived(createOdysejaQuery<Sponsor[][]>({
        queryKey: ['lappkaSponsors', String(cityId)],
        path: `/api/sponsor?cityId=${cityId}`,
    }));

    let rows = $derived.by(() => {
        const data = sponsorsQuery.data ?? [];
        return [...data, []];
    });

    let deleteMutation = $derived(createDelMutation<void, { imageId: number }>({
        path: (vars) => `/api/sponsor/${vars.imageId}`,
        queryKey: ['lappkaSponsors', String(cityId)],
        onSuccess: () => {
            toast.success('Sponsor usunięty');
        },
    }));

    async function uploadImage(file: File, row: number) {
        uploading = true;
        try {
            const formData = new FormData();
            formData.append('image', file);
            formData.append('row', String(row));

            formData.append('cityId', String(cityId));

            const res = await fetch('/api/proxy/sponsor', {
                method: 'POST',
                body: formData,
            });

            if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);

            toast.success('Sponsor dodany');
            await queryClient.invalidateQueries({queryKey: ['lappkaSponsors', String(cityId)]});
        } catch (err) {
            toast.error(`Błąd uploadu: ${err instanceof Error ? err.message : 'Nieznany błąd'}`);
        } finally {
            uploading = false;
        }
    }

    function handleFileInput(e: Event, row: number) {
        const input = e.target as HTMLInputElement;
        const file = input.files?.[0];
        if (file) {
            uploadImage(file, row);
            input.value = '';
        }
    }

    function sponsorImageUrl(sponsorId: number): string {
        return `/api/proxy/sponsor/${sponsorId}`;
    }
</script>

<div class="flex flex-col gap-6">
    <div class="flex items-center gap-3">
        <div class="flex items-center justify-center size-10 rounded-lg bg-primary/10">
            <IconHeart class="size-5 text-primary"/>
        </div>
        <div>
            <h1 class="text-xl font-semibold tracking-tight">Sponsorzy</h1>
            <p class="text-sm text-muted-foreground">Zarządzaj logotypami sponsorów</p>
        </div>
    </div>

    {#if sponsorsQuery.isPending}
        <div class="flex flex-col items-center justify-center py-16 gap-3">
            <Spinner size="sm"/>
            <p class="text-sm text-muted-foreground">Ładowanie sponsorów...</p>
        </div>
    {:else if sponsorsQuery.error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/5 p-6 text-center">
            <p class="font-medium text-destructive">Błąd ładowania</p>
        </div>
    {:else}
        <div class="flex flex-col gap-4">
            {#each rows as row, rowIndex (rowIndex)}
                <div class="rounded-lg border p-4">
                    <div class="flex items-center justify-between mb-3">
                        <p class="text-sm font-medium text-muted-foreground">Rząd {rowIndex + 1}</p>
                        <label class="inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 border border-input bg-background hover:bg-accent hover:text-accent-foreground h-9 px-3 cursor-pointer">
                            <IconPlus class="size-4"/>
                            Dodaj logo
                            <input
                                    type="file"
                                    accept="image/jpeg,image/png"
                                    class="hidden"
                                    onchange={(e) => handleFileInput(e, rowIndex)}
                            />
                        </label>
                    </div>
                    {#if row.length === 0}
                        <div class="flex items-center justify-center py-8 border-2 border-dashed rounded-md">
                            <label class="cursor-pointer text-center">
                                <IconUpload class="size-6 text-muted-foreground/40 mx-auto mb-1"/>
                                <p class="text-sm text-muted-foreground">Przeciągnij lub kliknij aby dodać logo</p>
                                <input
                                        type="file"
                                        accept="image/jpeg,image/png"
                                        class="hidden"
                                        onchange={(e) => handleFileInput(e, rowIndex)}
                                />
                            </label>
                        </div>
                    {:else}
                        <div class="flex flex-wrap gap-4">
                            {#each row as sponsor (sponsor.id)}
                                <div class="relative group rounded-lg border bg-white p-2">
                                    <img
                                            src={sponsorImageUrl(sponsor.id)}
                                            alt="Sponsor"
                                            class="h-20 w-auto object-contain"
                                    />
                                    <button
                                            class="absolute -top-2 -right-2 size-6 rounded-full bg-red-500 text-white flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity shadow-sm"
                                            onclick={() => deleteMutation.mutate({imageId: sponsor.id})}
                                            disabled={deleteMutation.isPending}
                                    >
                                        <IconTrash class="size-3"/>
                                    </button>
                                </div>
                            {/each}
                        </div>
                    {/if}
                </div>
            {/each}
        </div>
    {/if}
</div>
