<script lang="ts">
    import {createOdysejaQuery, createPostMutation} from "$lib/queries";
    import {currentUser} from "$lib/userStore";
    import {Button} from "$lib/components/ui/button/index.js";
    import * as Input from "$lib/components/ui/input/index.js";
    import {Spinner} from "$lib/components/ui/spinner";
    import SendIcon from "@lucide/svelte/icons/send";
    import XIcon from "@lucide/svelte/icons/x";
    import MessageCircleIcon from "@lucide/svelte/icons/message-circle";
    import {onMount, onDestroy, tick} from "svelte";
    import {apiFetch} from "$lib/api";
    import {Client} from "@stomp/stompjs";

    type ChatMessage = {
        id: number;
        performanceId: number;
        userId: string;
        userName: string;
        message: string;
        createdAt: string;
    };

    let {
        performanceId,
        onclose,
    }: {
        performanceId: string;
        onclose: () => void;
    } = $props();

    let messageInput = $state('');
    let messagesContainer: HTMLElement | null = $state(null);
    let user = $derived($currentUser);
    let wsMessages = $state<ChatMessage[]>([]);
    let stompClient: Client | null = null;

    // Load message history via REST
    let messagesQuery = createOdysejaQuery<ChatMessage[]>({
        queryKey: ['chat', performanceId],
        path: `/api/v1/form/${performanceId}/chat`,
    });

    // Combine REST history with WebSocket live messages
    let allMessages = $derived.by(() => {
        const history = messagesQuery.data ?? [];
        const historyIds = new Set(history.map((m: ChatMessage) => m.id));
        const newMsgs = wsMessages.filter((m: ChatMessage) => !historyIds.has(m.id));
        return [...history, ...newMsgs];
    });

    // Send via REST (goes through BFF proxy, fully authenticated)
    let sendMutation = createPostMutation<ChatMessage, { message: string }>({
        path: `/api/v1/form/${performanceId}/chat`,
        queryKey: ['chat', performanceId],
        onSuccess: () => {
            messageInput = '';
        }
    });

    // Auto-scroll to bottom when messages change
    $effect(() => {
        if (allMessages.length > 0 && messagesContainer) {
            tick().then(() => {
                messagesContainer?.scrollTo({
                    top: messagesContainer.scrollHeight,
                    behavior: 'smooth'
                });
            });
        }
    });

    async function connectWebSocket() {
        try {
            // Get one-time ticket via REST (authenticated through BFF proxy)
            const {ticket} = await apiFetch<{ ticket: string }>(
                `/api/v1/form/${performanceId}/chat/ws-ticket`,
                {method: 'POST'}
            );

            // Determine WebSocket URL
            const protocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
            const sockJsUrl = `${protocol}//${window.location.host}/ws`;

            stompClient = new Client({
                // SockJS-compatible: use HTTP URL, stompjs will use SockJS transport
                webSocketFactory: () => {
                    // Use SockJS via raw WebSocket endpoint with SockJS protocol
                    return new WebSocket(`${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/ws/websocket`);
                },
                connectHeaders: {
                    ticket: ticket,
                },
                reconnectDelay: 5000,
                onConnect: () => {
                    stompClient?.subscribe(`/topic/chat.${performanceId}`, (frame) => {
                        try {
                            const msg: ChatMessage = JSON.parse(frame.body);
                            // Only add if it's not from current user (they already see it via mutation)
                            // Actually, add all - dedup handles it via allMessages derived
                            wsMessages = [...wsMessages, msg];
                        } catch (e) {
                            console.error('Failed to parse chat message', e);
                        }
                    });
                },
                onStompError: (frame) => {
                    console.error('STOMP error', frame);
                },
            });

            stompClient.activate();
        } catch (e) {
            console.error('Failed to connect WebSocket', e);
        }
    }

    function disconnectWebSocket() {
        if (stompClient?.active) {
            stompClient.deactivate();
        }
        stompClient = null;
    }

    onMount(() => {
        connectWebSocket();
    });

    onDestroy(() => {
        disconnectWebSocket();
    });

    function handleSend() {
        const trimmed = messageInput.trim();
        if (!trimmed) return;
        sendMutation.mutate({message: trimmed});
    }

    function handleKeydown(event: KeyboardEvent) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            handleSend();
        }
    }

    function formatTime(isoString: string): string {
        const date = new Date(isoString);
        return date.toLocaleTimeString('pl-PL', {hour: '2-digit', minute: '2-digit'});
    }
</script>

<div class="fixed bottom-24 right-6 z-50 w-[380px] max-h-[500px] flex flex-col rounded-xl border bg-card shadow-xl overflow-hidden">
    <!-- Header -->
    <div class="flex items-center justify-between px-4 py-3 border-b bg-muted/30">
        <div class="flex items-center gap-2">
            <MessageCircleIcon class="size-4 text-muted-foreground"/>
            <span class="text-sm font-medium">Czat</span>
        </div>
        <button
            class="flex items-center justify-center size-7 rounded-full hover:bg-muted transition-colors cursor-pointer"
            onclick={onclose}
        >
            <XIcon class="size-4"/>
        </button>
    </div>

    <!-- Messages -->
    <div
        bind:this={messagesContainer}
        class="flex-1 overflow-y-auto p-4 space-y-3 min-h-[300px] max-h-[380px]"
    >
        {#if messagesQuery.isPending}
            <div class="flex items-center justify-center h-full">
                <Spinner size="sm"/>
            </div>
        {:else if allMessages.length === 0}
            <div class="flex flex-col items-center justify-center h-full text-center">
                <MessageCircleIcon class="size-8 text-muted-foreground/40 mb-2"/>
                <p class="text-sm text-muted-foreground">Brak wiadomości</p>
                <p class="text-xs text-muted-foreground/70">Napisz pierwszą wiadomość</p>
            </div>
        {:else}
            {#each allMessages as msg (msg.id)}
                {@const isOwn = msg.userId === user?.userId}
                <div class="flex flex-col {isOwn ? 'items-end' : 'items-start'}">
                    {#if !isOwn}
                        <span class="text-xs text-muted-foreground mb-0.5 px-1">{msg.userName}</span>
                    {/if}
                    <div
                        class="max-w-[85%] rounded-lg px-3 py-2 text-sm {isOwn ? 'bg-primary text-primary-foreground' : 'bg-muted'}"
                    >
                        {msg.message}
                    </div>
                    <span class="text-[10px] text-muted-foreground mt-0.5 px-1">{formatTime(msg.createdAt)}</span>
                </div>
            {/each}
        {/if}
    </div>

    <!-- Input -->
    <div class="flex items-center gap-2 px-3 py-3 border-t bg-muted/10">
        <Input.Input
            type="text"
            placeholder="Napisz wiadomość..."
            bind:value={messageInput}
            onkeydown={handleKeydown}
            class="flex-1 text-sm"
        />
        <Button
            size="icon"
            onclick={handleSend}
            disabled={sendMutation.isPending || !messageInput.trim()}
            class="shrink-0"
        >
            <SendIcon class="size-4"/>
        </Button>
    </div>
</div>
