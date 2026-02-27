<script lang="ts">
    import * as Card from '$lib/components/ui/card/index.js';
    import * as Input from '$lib/components/ui/input/index.js';
    import * as Label from '$lib/components/ui/label/index.js';
    import {Badge} from '$lib/components/ui/badge/index.js';
    import type {FormEntryType, ProblemForm} from "./types";
    import {formatSortIndex} from "./sortIndexUtils";
    import LanguagesIcon from "@lucide/svelte/icons/languages";

    interface Props {
        form: ProblemForm;
    }

    let {form = $bindable()}: Props = $props();

    function hasEntries(entries: FormEntryType[]): boolean {
        return entries.some((e: FormEntryType) => e.name.trim() !== '' || (e.entries && e.entries.length > 0));
    }
</script>

<div class="flex flex-col gap-6">
    <div class="flex items-center gap-3 px-1">
        <LanguagesIcon class="size-5 text-muted-foreground" />
        <div>
            <p class="text-sm font-medium">Tłumaczenia nazw wpisów</p>
            <p class="text-xs text-muted-foreground">Podaj angielskie tłumaczenie dla każdego wpisu. Puste pola zostaną zastąpione oryginalną nazwą.</p>
        </div>
    </div>

    <!-- DT Entries -->
    {#if form.dtEntries.length > 0}
        <Card.Root>
            <Card.Header>
                <Card.Title class="flex items-center gap-2">
                    <Badge variant="outline" class="font-mono">DT</Badge>
                    Punktacja długoterminowa
                </Card.Title>
            </Card.Header>
            <Card.Content>
                <div class="flex flex-col gap-3">
                    {#each form.dtEntries as entry, i}
                        {@const displayIndex = formatSortIndex(entry)}
                        <div class="flex flex-col gap-1">
                            <div class="flex items-center gap-3">
                                <span class="text-sm font-semibold text-muted-foreground min-w-[2rem]">{displayIndex}.</span>
                                <div class="flex-1 grid grid-cols-2 gap-3 items-center">
                                    <div class="text-sm font-medium truncate" title={entry.name}>{entry.name || '(brak nazwy)'}</div>
                                    <Input.Input
                                        type="text"
                                        placeholder="English translation..."
                                        bind:value={entry.translation}
                                        class="text-sm"
                                    />
                                </div>
                            </div>
                            {#if entry.entries && entry.entries.length > 0}
                                <div class="ml-8 flex flex-col gap-2 border-l-2 border-muted pl-4">
                                    {#each entry.entries as nested, j}
                                        {@const nestedIndex = formatSortIndex(nested, displayIndex)}
                                        <div class="flex items-center gap-3">
                                            <span class="text-xs font-semibold text-muted-foreground min-w-[2.5rem]">{nestedIndex}.</span>
                                            <div class="flex-1 grid grid-cols-2 gap-3 items-center">
                                                <div class="text-sm truncate" title={nested.name}>{nested.name || '(brak nazwy)'}</div>
                                                <Input.Input
                                                    type="text"
                                                    placeholder="English translation..."
                                                    bind:value={nested.translation}
                                                    class="text-sm"
                                                />
                                            </div>
                                        </div>
                                        {#if nested.entries && nested.entries.length > 0}
                                            <div class="ml-8 flex flex-col gap-2 border-l-2 border-muted pl-4">
                                                {#each nested.entries as deep, k}
                                                    {@const deepIndex = formatSortIndex(deep, nestedIndex)}
                                                    <div class="flex items-center gap-3">
                                                        <span class="text-xs font-semibold text-muted-foreground min-w-[3rem]">{deepIndex}.</span>
                                                        <div class="flex-1 grid grid-cols-2 gap-3 items-center">
                                                            <div class="text-sm truncate" title={deep.name}>{deep.name || '(brak nazwy)'}</div>
                                                            <Input.Input
                                                                type="text"
                                                                placeholder="English translation..."
                                                                bind:value={deep.translation}
                                                                class="text-sm"
                                                            />
                                                        </div>
                                                    </div>
                                                {/each}
                                            </div>
                                        {/if}
                                    {/each}
                                </div>
                            {/if}
                        </div>
                    {/each}
                </div>
            </Card.Content>
        </Card.Root>
    {/if}

    <!-- Style Entries -->
    {#if form.styleEntries.length > 0}
        <Card.Root>
            <Card.Header>
                <Card.Title class="flex items-center gap-2">
                    <Badge variant="outline" class="font-mono">Styl</Badge>
                    Styl
                </Card.Title>
            </Card.Header>
            <Card.Content>
                <div class="flex flex-col gap-3">
                    {#each form.styleEntries as entry, i}
                        {@const displayIndex = (i + 1).toString()}
                        <div class="flex items-center gap-3">
                            <span class="text-sm font-semibold text-muted-foreground min-w-[2rem]">{displayIndex}.</span>
                            <div class="flex-1 grid grid-cols-2 gap-3 items-center">
                                <div class="text-sm font-medium truncate" title={entry.name}>{entry.name || '(brak nazwy)'}</div>
                                <Input.Input
                                    type="text"
                                    placeholder="English translation..."
                                    bind:value={entry.translation}
                                    class="text-sm"
                                />
                            </div>
                        </div>
                    {/each}
                </div>
            </Card.Content>
        </Card.Root>
    {/if}

    <!-- Penalty Entries -->
    {#if form.penaltyEntries.length > 0}
        <Card.Root>
            <Card.Header>
                <Card.Title class="flex items-center gap-2">
                    <Badge variant="outline" class="font-mono">Karne</Badge>
                    Karne
                </Card.Title>
            </Card.Header>
            <Card.Content>
                <div class="flex flex-col gap-3">
                    {#each form.penaltyEntries as entry, i}
                        {@const displayIndex = (i + 1).toString()}
                        <div class="flex items-center gap-3">
                            <span class="text-sm font-semibold text-muted-foreground min-w-[2rem]">{displayIndex}.</span>
                            <div class="flex-1 grid grid-cols-2 gap-3 items-center">
                                <div class="text-sm font-medium truncate" title={entry.name}>{entry.name || '(brak nazwy)'}</div>
                                <Input.Input
                                    type="text"
                                    placeholder="English translation..."
                                    bind:value={entry.translation}
                                    class="text-sm"
                                />
                            </div>
                        </div>
                    {/each}
                </div>
            </Card.Content>
        </Card.Root>
    {/if}

    {#if form.dtEntries.length === 0 && form.styleEntries.length === 0 && form.penaltyEntries.length === 0}
        <div class="rounded-lg border border-dashed p-12 text-center">
            <LanguagesIcon class="size-10 text-muted-foreground/40 mx-auto mb-3" />
            <p class="text-muted-foreground font-medium">Brak wpisów do przetłumaczenia</p>
            <p class="text-sm text-muted-foreground/70 mt-1">Najpierw dodaj wpisy w zakładce Formularz.</p>
        </div>
    {/if}
</div>
