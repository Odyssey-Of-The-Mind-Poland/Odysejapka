<script>
    import {ListBox, ListBoxItem} from "@skeletonlabs/skeleton";
    import {fade} from 'svelte/transition';

    export let stages = [];
    export let ages = [];
    export let problems = [];

    export let selectedStages = [];
    export let selectedAges = [];
    export let selectedProblems = [];

    let isContentVisible = false;

    function toggleContentVisibility() {
        isContentVisible = !isContentVisible;
    }
</script>

<div class="card p-3 mb-5 space-y-3">
    <header class="flex justify-between items-center">
        <div>Filtry</div>
        <button class="btn" on:click={toggleContentVisibility}>
            {#if isContentVisible}
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"
                     class="h-6 w-6 inline-block align-middle">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M6 18L18 6M6 6l12 12"></path>
                </svg>
            {/if}
            {#if !isContentVisible}
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"
                     class="h-6 w-6 inline-block align-middle">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M4 6h16M4 12h16M4 18h16"></path>
                </svg>
            {/if}
        </button>
    </header>

    {#if isContentVisible}
        <section class="flex flex-wrap space-x-5" transition:fade>
            <div class="flex-grow">
                <ListBox multiple>
                    {#each stages as stage}
                        <ListBoxItem bind:group={selectedStages} name="stage" value={stage}>Scena {stage}</ListBoxItem>
                    {/each}
                </ListBox>
            </div>

            <div class="flex-grow">
                <ListBox multiple>
                    {#each ages as age}
                        <ListBoxItem bind:group={selectedAges} name="stage" value={age}>Grupa
                            wiekowa {age}</ListBoxItem>
                    {/each}
                </ListBox>
            </div>

            <div class="flex-grow">
                <ListBox multiple>
                    {#each problems as prblem}
                        <ListBoxItem bind:group={selectedProblems} name="stage" value={prblem}>
                            Problem {prblem}</ListBoxItem>
                    {/each}
                </ListBox>
            </div>
        </section>
    {/if}
</div>
