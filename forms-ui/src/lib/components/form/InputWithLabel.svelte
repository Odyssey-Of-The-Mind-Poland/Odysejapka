<script lang="ts">
    import {Input} from '$lib/components/ui/input';
    import type {HTMLInputAttributes} from 'svelte/elements';

    interface Props extends Omit<HTMLInputAttributes, 'id' | 'value'> {
        label: string;
        value: string;
        id?: string | number | null;
        class?: string;
        flexClass?: string;
        onInput?: (e: Event & { currentTarget: HTMLInputElement }) => void;
    }

    let {label, value = $bindable(), id, class: className = '', flexClass = 'flex-1', onInput, type, ...inputProps}: Props = $props();
    
    let inputId = id?.toString() || `input-${Math.random().toString(36).substr(2, 9)}`;
    let inputType = type ?? 'text';
</script>

<div class="group relative {flexClass}">
    <label
            class="absolute top-0 block pointer-events-none cursor-default px-2 text-xs font-medium text-foreground"
            for={inputId}
    >
        <span class="inline-flex bg-background px-1">{label}</span>
    </label>
    <Input 
        bind:value={value} 
        class="w-full dark:bg-background pt-5 {className}" 
        placeholder=" "
        type={inputType}
        id={inputId}
        oninput={onInput}
        {...(inputProps as any)}
    />
</div>

