<script lang="ts">
    import * as Breadcrumb from "$lib/components/ui/breadcrumb/index.js";
    import {breadcrumbs} from '$lib/breadcrumbs';

    $: list = $breadcrumbs;
</script>

<header
        class="bg-background/90 h-(--header-height) group-has-data-[collapsible=icon]/sidebar-wrapper:h-(--header-height) sticky top-0 z-10 flex shrink-0 items-center gap-2 border-b transition-[width,height] ease-linear"
>
    <div class="flex w-full items-center gap-1 px-4 lg:gap-2 lg:px-6">
        <div class="flex items-center">
            <Breadcrumb.Root>
                <Breadcrumb.List>
                    {#each list as c, i}
                        <Breadcrumb.Item>
                            {#if !c.current}
                                <Breadcrumb.Link href={c.href}>{c.name}</Breadcrumb.Link>
                            {:else}
                                <Breadcrumb.Page>{c.name}</Breadcrumb.Page>
                            {/if}
                        </Breadcrumb.Item>
                        {#if i < list.length - 1}
                            <Breadcrumb.Separator/>
                        {/if}
                    {/each}
                </Breadcrumb.List>
            </Breadcrumb.Root>
        </div>
    </div>
    <div class="pr-4">
        <slot/>
    </div>
</header>

