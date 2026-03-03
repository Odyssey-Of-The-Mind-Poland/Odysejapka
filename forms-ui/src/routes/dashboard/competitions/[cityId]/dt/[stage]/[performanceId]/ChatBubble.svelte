<script lang="ts">
    import MessageCircleIcon from "@lucide/svelte/icons/message-circle";

    let {
        unreadCount = 0,
        onclick,
    }: {
        unreadCount: number;
        onclick: () => void;
    } = $props();
</script>

<button
    class="fixed bottom-6 right-6 z-50 flex items-center justify-center size-14 rounded-full bg-primary text-primary-foreground shadow-lg hover:bg-primary/90 transition-all hover:scale-105 cursor-pointer"
    onclick={onclick}
    aria-label="Otwórz czat"
>
    <MessageCircleIcon class="size-6" />
    {#if unreadCount > 0}
        <span class="absolute -top-1 -right-1 flex items-center justify-center size-5 rounded-full bg-destructive text-white text-xs font-bold">
            {unreadCount > 9 ? '9+' : unreadCount}
        </span>
    {/if}
</button>
