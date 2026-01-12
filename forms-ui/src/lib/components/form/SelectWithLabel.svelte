<script lang="ts">
    import * as Select from '$lib/components/ui/select/index.js';

    interface Props {
        label: string;
        value?: string | null;
        id?: string | number | null;
        flexClass?: string;
        disabled?: boolean;
        placeholder?: string;
        class?: string;
        onValueChange?: (value: string | undefined) => void;
        triggerContent: import('svelte').Snippet | (() => string);
        children: import('svelte').Snippet;
    }

    let {
        label, 
        value = $bindable(), 
        id, 
        flexClass = 'flex-1',
        disabled = false,
        placeholder = ' ',
        class: className = '',
        onValueChange,
        triggerContent,
        children
    }: Props = $props();
    
    let isFunction = typeof triggerContent === 'function';
    let triggerText = $derived(isFunction ? (triggerContent as () => string)() : '');
    let triggerSnippet = $derived(!isFunction ? (triggerContent as import('svelte').Snippet) : undefined);
    
    let selectId = id?.toString() || `select-${Math.random().toString(36).substr(2, 9)}`;
    let selectValue = $state(value ?? undefined);
    
    $effect(() => {
        selectValue = value ?? undefined;
    });
    
    $effect(() => {
        value = selectValue ?? null;
    });
</script>

<div class="group relative {flexClass}">
    <label
            class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
            for={selectId}
    >
        <span class="inline-flex bg-background px-1">{label}</span>
    </label>
    <Select.Root
        type="single"
        bind:value={selectValue}
        disabled={disabled}
        onValueChange={onValueChange}
    >
        <Select.Trigger class="pt-5 w-full {className}" id={selectId} disabled={disabled}>
            {#if isFunction}
                {triggerText}
            {:else if triggerSnippet}
                {@render triggerSnippet()}
            {/if}
        </Select.Trigger>
        <Select.Content>
            {@render children()}
        </Select.Content>
    </Select.Root>
</div>

