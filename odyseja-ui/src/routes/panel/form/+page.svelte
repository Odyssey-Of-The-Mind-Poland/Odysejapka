<script lang="ts">
    import { onMount } from 'svelte';
    import { get, put, post } from '$lib/apiService'; // ⬅ add `post`

    type CalcType = 'SUM' | 'AVERAGE';
    type FormEntry = { name: string; calcType: CalcType };

    // UI state
    let loading = false;
    let saving = false;
    let error: string | null = null;
    let success = false;

    // judge-specific state
    let loadingJudge = false;
    let savingJudge = false;
    let judgeCount = 0;

    // problem selector (select binds strings; keep a numeric mirror)
    let selectedProblem = '0';
    $: selectedProblemNum = Number(selectedProblem);

    let entries: FormEntry[] = [];

    let mounted = false;
    onMount(async () => {
        mounted = true;
        await Promise.all([
            loadEntries(selectedProblemNum),
            loadJudgeCount(selectedProblemNum),
        ]);
    });

    // Refetch when the problem changes
    $: if (mounted) {
        loadEntries(selectedProblemNum);
        loadJudgeCount(selectedProblemNum);
    }

    async function loadEntries(problem: number) {
        loading = true;
        error = null;
        success = false;
        try {
            const data = await get(`/api/v1/form/${problem}`);
            entries = Array.isArray(data) ? data : [];
        } catch (e: any) {
            error = e?.message ?? 'Failed to load form entries.';
            entries = [];
        } finally {
            loading = false;
        }
    }

    async function loadJudgeCount(problem: number) {
        loadingJudge = true;
        try {
            const data = await get(`/api/v1/form/${problem}/judge-count`);
            judgeCount = Number.isFinite(Number(data)) ? Number(data) : 0;
        } catch (e: any) {
            // don't nuke existing entries error state unless this fails alone
            error = e?.message ?? 'Failed to load judge count.';
            judgeCount = 0;
        } finally {
            loadingJudge = false;
        }
    }

    function addEntry() {
        entries = [...entries, { name: '', calcType: 'SUM' }];
    }

    function removeEntry(index: number) {
        entries = entries.filter((_, i) => i !== index);
    }

    async function save() {
        saving = true;
        error = null;
        success = false;
        try {
            await put(entries, `/api/v1/form/${selectedProblemNum}`, 'Form entries saved.');
            success = true;
        } catch (e: any) {
            error = e?.message ?? 'Failed to save form entries.';
        } finally {
            saving = false;
        }
    }

    async function saveJudge() {
        savingJudge = true;
        error = null;
        success = false;
        try {
            // backend expects a JSON number in the body
            await post(Number(judgeCount), `/api/v1/form/${selectedProblemNum}/judge-count`, 'Judge count saved.');
            success = true;
        } catch (e: any) {
            error = e?.message ?? 'Failed to save judge count.';
        } finally {
            savingJudge = false;
        }
    }
</script>

<div class="container mx-auto p-6 space-y-8">
    <header class="space-y-2">
        <h1 class="text-2xl font-bold">Form Entries</h1>
        <p class="opacity-70">Pick a problem, edit rows, then save.</p>
    </header>

    <!-- Problem selector -->
    <div class="max-w-sm">
        <label class="label w-full">
            <span class="label-text">Problem</span>
            <select class="select" bind:value={selectedProblem} aria-label="Select problem">
                {#each [0, 1, 2, 3, 4, 5] as p}
                    <option value={String(p)}>Problem {p}</option>
                {/each}
            </select>
        </label>
    </div>


    {#if loading || loadingJudge}
        <div class="p-3 rounded preset-tonal-surface">Loading…</div>
    {/if}
    {#if error}
        <div class="p-3 rounded preset-tonal-error">{error}</div>
    {/if}
    <!-- Judges section -->
    <section class="space-y-4">
        <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold">Judges</h2>
        </div>

        <div class="flex items-center gap-3">
            <label class="label">
                <span class="label-text">Judge count</span>
                <input
                        class="input w-28"
                        type="number"
                        min="0"
                        step="1"
                        bind:value={judgeCount}
                        on:change={() => (judgeCount = Number(judgeCount))}
                        aria-label="Judge count" />
            </label>

            <button
                    type="button"
                    class="btn variant-filled-primary"
                    on:click={saveJudge}
                    disabled={savingJudge || loadingJudge}>
                {#if savingJudge}Saving…{/if}{#if !savingJudge}Save judge count{/if}
            </button>
        </div>
    </section>

    <!-- Entries section (unchanged) -->
    <section class="space-y-4">
        <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold">Entries</h2>
            <button type="button" class="btn variant-filled-primary" on:click={addEntry}>Add entry</button>
        </div>

        <div class="grid grid-cols-[1fr_160px_40px] gap-3 px-2 text-sm opacity-70">
            <div>Name</div>
            <div>Type</div>
            <div class="text-right"> </div>
        </div>

        <div class="space-y-3">
            {#if entries.length === 0 && !loading}
                <div class="p-3 rounded preset-tonal-surface">No entries. Click “Add entry”.</div>
            {/if}

            {#each entries as entry, i (i)}
                <div class="grid grid-cols-[1fr_160px_40px] gap-3 items-center">
                    <label class="label w-full">
                        <span class="sr-only">Name</span>
                        <input
                                class="input w-full"
                                type="text"
                                placeholder="Entry name"
                                bind:value={entry.name} />
                    </label>

                    <label class="label">
                        <span class="sr-only">Type</span>
                        <select class="select" bind:value={entry.calcType}>
                            <option value="SUM">SUM</option>
                            <option value="AVERAGE">AVERAGE</option>
                        </select>
                    </label>

                    <button
                            type="button"
                            class="btn preset-tonal-error"
                            on:click={() => removeEntry(i)}
                            title="Remove this row"
                            aria-label="Remove row">
                        ✕
                    </button>
                </div>
            {/each}
        </div>
    </section>

    <div class="flex gap-3">
        <button type="button" class="btn variant-filled-primary" on:click={save} disabled={saving || loading}>
            {#if saving}Saving…{/if}{#if !saving}Save{/if}
        </button>
    </div>
</div>
