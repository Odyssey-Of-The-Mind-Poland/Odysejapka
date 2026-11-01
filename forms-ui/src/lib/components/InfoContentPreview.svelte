<script lang="ts">
    import ArrowLeftIcon from '@lucide/svelte/icons/arrow-left';
    import HeartIcon from '@lucide/svelte/icons/heart';
    import '$lib/infoContent.css';

    let {
        title,
        html,
        theme = 'light',
    }: {
        title: string;
        html: string;
        theme?: 'light' | 'dark';
    } = $props();
</script>

<!-- The info screen of the mobile app: neutral top bar (back, title, favourite) above the scrolling content. -->
<div class="info-content mx-auto flex h-[640px] w-[375px] flex-col overflow-hidden rounded-[32px] border-[6px] border-neutral-800 shadow-md"
     data-theme={theme}>
    <div class="flex shrink-0 items-center justify-between gap-2 border-b-2 px-4 pb-3 pt-4"
         style="background:var(--ic-surface);border-color:var(--ic-border)">
        <span class="flex size-10 shrink-0 items-center justify-center rounded-full border-2"
              style="border-color:var(--ic-border);color:var(--ic-heading)">
            <ArrowLeftIcon class="size-5"/>
        </span>
        <span class="line-clamp-2 text-center text-base font-medium leading-6" style="color:var(--ic-heading)">
            {title || 'Tytuł informacji'}
        </span>
        <span class="flex size-10 shrink-0 items-center justify-center rounded-full border-2"
              style="border-color:var(--ic-border);color:var(--ic-text)">
            <HeartIcon class="size-5"/>
        </span>
    </div>
    <div class="min-h-0 flex-1 overflow-y-auto p-4">
        {@html html}
    </div>
</div>
