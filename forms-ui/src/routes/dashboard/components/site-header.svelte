<script lang="ts">
    import * as Breadcrumb from "$lib/components/ui/breadcrumb/index.js";
    import * as Sidebar from "$lib/registry/ui/sidebar/index.js";
    import {Separator} from "$lib/components/ui/separator/index.js";
    import {breadcrumbs} from '$lib/breadcrumbs';

    $: list = $breadcrumbs;
</script>

<header
        class="bg-background/90 h-(--header-height) group-has-data-[collapsible=icon]/sidebar-wrapper:h-(--header-height) sticky top-0 z-10 flex shrink-0 items-center gap-2 border-b transition-[width,height] ease-linear"
>
    <div class="flex w-full min-w-0 items-center gap-1 px-3 sm:px-4 lg:gap-2 lg:px-6">
        <Sidebar.Trigger class="-ml-1 size-9 shrink-0 md:hidden"/>
        <Separator orientation="vertical" class="mr-1 !h-4 shrink-0 md:hidden"/>
        <div class="flex min-w-0 items-center">
            <!-- The breadcrumb primitives are legacy Svelte 4 components whose
                 `className` replaces the defaults instead of merging, so every
                 class below has to be spelled out in full. -->
            <Breadcrumb.Root className="min-w-0">
                <!-- Below `sm` only the current crumb is shown: ancestors stay
                     reachable through the sidebar, so hiding them keeps the one
                     crumb that says where you are from being truncated. -->
                <Breadcrumb.List
                        className="flex flex-nowrap items-center gap-1.5 text-sm text-muted-foreground sm:gap-2.5 min-w-0"
                >
                    {#each list as c, i}
                        <Breadcrumb.Item
                                className={c.current
                                    ? 'inline-flex items-center gap-1.5 min-w-0'
                                    : 'hidden sm:inline-flex items-center gap-1.5'}
                        >
                            {#if !c.current}
                                <Breadcrumb.Link
                                        href={c.href}
                                        className="transition-colors hover:text-foreground whitespace-nowrap"
                                >{c.name}</Breadcrumb.Link>
                            {:else}
                                <Breadcrumb.Page className="font-normal text-foreground truncate">
                                    {c.name}
                                </Breadcrumb.Page>
                            {/if}
                        </Breadcrumb.Item>
                        {#if i < list.length - 1}
                            <Breadcrumb.Separator className="hidden sm:inline text-muted-foreground/60"/>
                        {/if}
                    {/each}
                </Breadcrumb.List>
            </Breadcrumb.Root>
        </div>
    </div>
    <div class="shrink-0 pr-3 sm:pr-4">
        <slot/>
    </div>
</header>
