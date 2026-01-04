<script lang="ts">
    import { onMount } from 'svelte';
    import { page } from '$app/stores';
    import { get, put } from '$lib/apiService';

    type CalcType = 'SUM' | 'AVERAGE';
    type FormCategory = 'DT' | 'STYLE' | 'PENALTY';
    // entries from backend always have an id + category here
    type FormEntry = { id: number; name: string; calcType: CalcType; category: FormCategory };
    type FormResult = { entryId: number; judge: number; result: number };
    type Performance = {
        id: number;
        team: string;
        problem: number;
        city?: string;
        performance?: string;
    };

    // route param
    let performanceId = 0;
    $: performanceId = Number($page.params.performanceId);

    // UI state
    let loading = true;
    let saving = false;
    let error: string | null = null;
    let success = false;

    // data
    let perf: Performance | null = null;
    let entries: FormEntry[] = [];
    let judgeCount = 0;

    // results map: key `${entryId}:${judge}` -> number
    let valueMap: Record<string, number> = {};

    function keyOf(entryId: number, judge: number) {
        return `${entryId}:${judge}`;
    }

    async function loadAll() {
        loading = true;
        error = null;
        success = false;
        valueMap = {};

        try {
            // 1) performance -> get problem
            perf = await get(`/timeTable/${performanceId}`);
            const problem = Number(perf?.problem ?? 0);

            // 2) form entries for problem (now include category)
            entries = await get(`/api/v1/form/${problem}`);

            // 3) judge count for problem
            judgeCount = await get(`/api/v1/form/${problem}/judge-count`);
            if (!Number.isFinite(judgeCount) || judgeCount < 0) judgeCount = 0;

            // 4) existing results for performance
            const existing: FormResult[] = await get(`/api/v1/form/${performanceId}/result`);

            // seed valueMap with existing values
            for (const r of existing ?? []) {
                if (r && typeof r.entryId === 'number' && typeof r.judge === 'number') {
                    const k = keyOf(r.entryId, r.judge);
                    const v = Number(r.result ?? 0);
                    if (Number.isFinite(v)) valueMap[k] = v;
                }
            }

            // ensure we at least have zeros for visible inputs (optional)
            for (const e of entries) {
                for (let j = 0; j < judgeCount; j++) {
                    const k = keyOf(e.id, j);
                    if (!(k in valueMap)) valueMap[k] = 0;
                }
            }
        } catch (e: any) {
            error = e?.message ?? 'Failed to load performance form.';
            entries = [];
            judgeCount = 0;
        } finally {
            loading = false;
        }
    }

    onMount(loadAll);

    function setCell(entryId: number, judge: number, value: string) {
        const n = Number(value);
        if (Number.isFinite(n)) {
            valueMap[keyOf(entryId, judge)] = n;
        }
    }

    function aggregate(entry: FormEntry): number {
        const vals: number[] = [];
        for (let j = 0; j < judgeCount; j++) {
            const n = valueMap[keyOf(entry.id, j)];
            if (Number.isFinite(n)) vals.push(n);
        }
        if (vals.length === 0) return 0;
        const sum = vals.reduce((a, b) => a + b, 0);
        return entry.calcType === 'AVERAGE' ? sum / vals.length : sum;
    }

    // ---- Grouping & subtotals ----
    const categories: { key: FormCategory; title: string }[] = [
        { key: 'DT', title: 'DT Entries' },
        { key: 'STYLE', title: 'Style Entries' },
        { key: 'PENALTY', title: 'Penalty Entries' }
    ];

    function entriesFor(cat: FormCategory): FormEntry[] {
        return entries.filter(e => e.category === cat);
    }

    function categoryTotal(cat: FormCategory): number {
        return entriesFor(cat).reduce((acc, e) => acc + aggregate(e), 0);
    }

    async function save() {
        saving = true;
        error = null;
        success = false;

        try {
            // build payload: only include finite numbers
            const payload = {
                results: [] as { entryId: number; judge: number; result: number }[],
            };

            for (const e of entries) {
                for (let j = 0; j < judgeCount; j++) {
                    const v = valueMap[keyOf(e.id, j)];
                    if (Number.isFinite(v)) {
                        payload.results.push({ entryId: e.id, judge: j, result: Number(v) });
                    }
                }
            }

            await put(payload, `/api/v1/form/${performanceId}/result`, 'Results saved.');
            success = true;
        } catch (e: any) {
            error = e?.message ?? 'Failed to save results.';
        } finally {
            saving = false;
        }
    }
</script>

<div class="container mx-auto p-6 space-y-8">
    <header class="space-y-2">
        <h1 class="text-2xl font-bold">Team Results</h1>
        {#if perf}
            <p class="opacity-70">
                Team: <span class="font-medium">{perf.team}</span>
                {#if perf.city}&nbsp;• City: {perf.city}{/if}
                &nbsp;• Problem: <span class="font-medium">{perf.problem}</span>
            </p>
        {/if}
    </header>

    {#if loading}
        <div class="p-3 rounded preset-tonal-surface">Loading…</div>
    {/if}
    {#if error}
        <div class="p-3 rounded preset-tonal-error">{error}</div>
    {/if}
    {#if success}
        <div class="p-3 rounded preset-tonal-success">Saved!</div>
    {/if}

    {#if !loading && entries.length === 0}
        <div class="p-3 rounded preset-tonal-surface">No form entries for this problem.</div>
    {/if}

    {#if !loading && entries.length > 0}
        {#each categories as c (c.key)}
            <section class="space-y-4">
                <div class="flex items-center justify-between">
                    <h2 class="text-lg font-semibold">{c.title}</h2>
                </div>

                <!-- Header row -->
                <div class="grid gap-2 items-center"
                     style="grid-template-columns: minmax(200px,1fr) repeat({Math.max(judgeCount,1)}, 120px) 160px;">
                    <div class="px-2 text-sm opacity-70">Entry</div>
                    {#each Array(judgeCount) as _, j}
                        <div class="px-2 text-sm opacity-70">Judge {j + 1}</div>
                    {/each}
                    <div class="px-2 text-sm opacity-70">Aggregate</div>
                </div>

                <!-- Rows for this category -->
                <div class="space-y-2">
                    {#each entriesFor(c.key) as e (e.id)}
                        <div class="grid gap-2 items-center"
                             style="grid-template-columns: minmax(200px,1fr) repeat({Math.max(judgeCount,1)}, 120px) 160px;">
                            <div class="px-2">
                                <div>{e.name}</div>
                                <div class="text-xs opacity-60">({e.calcType})</div>
                            </div>

                            {#each Array(judgeCount) as _, j}
                                <input
                                        class="input w-full"
                                        type="number"
                                        min="0"
                                        step="1"
                                        bind:value={valueMap[`${e.id}:${j}`]}
                                        on:input={(ev) => setCell(e.id, j, (ev.currentTarget).value)}
                                        aria-label={`Result for ${e.name}, judge ${j+1}`} />
                            {/each}

                            <div class="px-2 font-medium tabular-nums">
                                {aggregate(e).toFixed(2)}
                            </div>
                        </div>
                    {/each}
                </div>

                <!-- Subtotal row -->
                <div class="grid gap-2 items-center font-semibold"
                     style="grid-template-columns: minmax(200px,1fr) repeat({Math.max(judgeCount,1)}, 120px) 160px;">
                    <div class="px-2">Subtotal ({c.key})</div>
                    {#each Array(judgeCount) as judge}<div></div>{/each}
                    <div class="px-2 tabular-nums text-green-300">{categoryTotal(c.key).toFixed(2)}</div>
                </div>
            </section>
        {/each}

        <div class="flex gap-3">
            <button class="btn variant-filled-primary" on:click={save} disabled={saving || loading}>
                {#if saving}Saving…{/if}{#if !saving}Save{/if}
            </button>
            <button class="btn" on:click={loadAll} disabled={saving || loading}>Reload</button>
        </div>
    {/if}
</div>
