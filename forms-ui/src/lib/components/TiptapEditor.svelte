<script lang="ts">
    import {onMount, onDestroy} from 'svelte';
    import {Editor} from '@tiptap/core';
    import StarterKit from '@tiptap/starter-kit';
    import Link from '@tiptap/extension-link';
    import Underline from '@tiptap/extension-underline';
    import {TextStyle} from '@tiptap/extension-text-style';
    import {Color} from '@tiptap/extension-color';
    import {Highlight} from '@tiptap/extension-highlight';
    import {TextAlign} from '@tiptap/extension-text-align';

    let {value = $bindable(''), placeholder = ''}: { value: string; placeholder?: string } = $props();

    let element: HTMLDivElement;
    let editor: Editor | undefined;

    const textColors = [
        {label: 'Domyślny', value: ''},
        {label: 'Czerwony', value: '#dc2626'},
        {label: 'Pomarańczowy', value: '#ea580c'},
        {label: 'Zielony', value: '#16a34a'},
        {label: 'Niebieski', value: '#2563eb'},
        {label: 'Fioletowy', value: '#9333ea'},
        {label: 'Szary', value: '#6b7280'},
    ];

    const highlightColors = [
        {label: 'Brak', value: ''},
        {label: 'Żółty', value: '#fef08a'},
        {label: 'Zielony', value: '#bbf7d0'},
        {label: 'Niebieski', value: '#bfdbfe'},
        {label: 'Różowy', value: '#fbcfe8'},
        {label: 'Pomarańczowy', value: '#fed7aa'},
    ];

    let showColorPicker = $state(false);
    let showHighlightPicker = $state(false);

    onMount(() => {
        editor = new Editor({
            element,
            extensions: [
                StarterKit,
                Underline,
                Link.configure({openOnClick: false}),
                TextStyle,
                Color,
                Highlight.configure({multicolor: true}),
                TextAlign.configure({types: ['heading', 'paragraph']}),
            ],
            content: value,
            editorProps: {
                attributes: {
                    class: 'prose prose-sm max-w-none focus:outline-none min-h-[120px] px-3 py-2',
                },
            },
            onTransaction: () => {
                editor = editor;
            },
            onUpdate: ({editor: e}) => {
                value = e.getHTML();
            },
        });
    });

    onDestroy(() => {
        editor?.destroy();
    });

    function toggleBold() { editor?.chain().focus().toggleBold().run(); }
    function toggleItalic() { editor?.chain().focus().toggleItalic().run(); }
    function toggleUnderline() { editor?.chain().focus().toggleUnderline().run(); }
    function toggleStrike() { editor?.chain().focus().toggleStrike().run(); }
    function toggleBulletList() { editor?.chain().focus().toggleBulletList().run(); }
    function toggleOrderedList() { editor?.chain().focus().toggleOrderedList().run(); }
    function toggleHeading(level: 1 | 2 | 3) { editor?.chain().focus().toggleHeading({level}).run(); }
    function toggleBlockquote() { editor?.chain().focus().toggleBlockquote().run(); }
    function setHorizontalRule() { editor?.chain().focus().setHorizontalRule().run(); }

    function setLink() {
        const url = prompt('URL:');
        if (url) {
            editor?.chain().focus().setLink({href: url}).run();
        } else {
            editor?.chain().focus().unsetLink().run();
        }
    }

    function setColor(color: string) {
        if (color) {
            editor?.chain().focus().setColor(color).run();
        } else {
            editor?.chain().focus().unsetColor().run();
        }
        showColorPicker = false;
    }

    function setHighlight(color: string) {
        if (color) {
            editor?.chain().focus().setHighlight({color}).run();
        } else {
            editor?.chain().focus().unsetHighlight().run();
        }
        showHighlightPicker = false;
    }

    function setAlign(align: string) {
        editor?.chain().focus().setTextAlign(align).run();
    }

    function handleClickOutside(e: MouseEvent) {
        const target = e.target as HTMLElement;
        if (!target.closest('.color-picker-wrapper')) showColorPicker = false;
        if (!target.closest('.highlight-picker-wrapper')) showHighlightPicker = false;
    }
</script>

<svelte:document onclick={handleClickOutside}/>

<div class="rounded-md border border-input bg-background overflow-hidden">
    <div class="flex flex-wrap gap-0.5 border-b bg-muted/30 px-1 py-1 items-center">
        <button type="button" class="toolbar-btn" class:active={editor?.isActive('bold')} onclick={toggleBold} title="Pogrubienie">
            <b>B</b>
        </button>
        <button type="button" class="toolbar-btn" class:active={editor?.isActive('italic')} onclick={toggleItalic} title="Kursywa">
            <i>I</i>
        </button>
        <button type="button" class="toolbar-btn" class:active={editor?.isActive('underline')} onclick={toggleUnderline} title="Podkreślenie">
            <u>U</u>
        </button>
        <button type="button" class="toolbar-btn" class:active={editor?.isActive('strike')} onclick={toggleStrike} title="Przekreślenie">
            <s>S</s>
        </button>

        <div class="w-px h-5 bg-border mx-0.5"></div>

        <!-- Text color -->
        <div class="relative color-picker-wrapper">
            <button
                type="button"
                class="toolbar-btn"
                title="Kolor tekstu"
                onclick={(e) => { e.stopPropagation(); showHighlightPicker = false; showColorPicker = !showColorPicker; }}
            >
                <span style="border-bottom: 2px solid {editor?.getAttributes('textStyle').color || '#000'}">A</span>
            </button>
            {#if showColorPicker}
                <div class="color-dropdown" onclick={(e) => e.stopPropagation()}>
                    {#each textColors as c}
                        <button
                            type="button"
                            class="color-swatch"
                            style="background: {c.value || '#000'}"
                            title={c.label}
                            onclick={() => setColor(c.value)}
                        >
                            {#if !c.value}✕{/if}
                        </button>
                    {/each}
                </div>
            {/if}
        </div>

        <!-- Highlight -->
        <div class="relative highlight-picker-wrapper">
            <button
                type="button"
                class="toolbar-btn"
                title="Zaznaczenie"
                onclick={(e) => { e.stopPropagation(); showColorPicker = false; showHighlightPicker = !showHighlightPicker; }}
            >
                <span class="highlight-icon">ab</span>
            </button>
            {#if showHighlightPicker}
                <div class="color-dropdown" onclick={(e) => e.stopPropagation()}>
                    {#each highlightColors as c}
                        <button
                            type="button"
                            class="color-swatch"
                            style="background: {c.value || '#e5e7eb'}"
                            title={c.label}
                            onclick={() => setHighlight(c.value)}
                        >
                            {#if !c.value}✕{/if}
                        </button>
                    {/each}
                </div>
            {/if}
        </div>

        <div class="w-px h-5 bg-border mx-0.5"></div>

        <button type="button" class="toolbar-btn" class:active={editor?.isActive('heading', {level: 2})} onclick={() => toggleHeading(2)} title="Nagłówek">
            H
        </button>
        <button type="button" class="toolbar-btn" class:active={editor?.isActive('bulletList')} onclick={toggleBulletList} title="Lista">
            &#8226;
        </button>
        <button type="button" class="toolbar-btn" class:active={editor?.isActive('orderedList')} onclick={toggleOrderedList} title="Lista numerowana">
            1.
        </button>
        <button type="button" class="toolbar-btn" class:active={editor?.isActive('blockquote')} onclick={toggleBlockquote} title="Cytat">
            &ldquo;
        </button>

        <div class="w-px h-5 bg-border mx-0.5"></div>

        <!-- Alignment -->
        <button type="button" class="toolbar-btn" class:active={editor?.isActive({textAlign: 'left'})} onclick={() => setAlign('left')} title="Do lewej">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="17" y1="10" x2="3" y2="10"/><line x1="21" y1="6" x2="3" y2="6"/><line x1="21" y1="14" x2="3" y2="14"/><line x1="17" y1="18" x2="3" y2="18"/></svg>
        </button>
        <button type="button" class="toolbar-btn" class:active={editor?.isActive({textAlign: 'center'})} onclick={() => setAlign('center')} title="Wyśrodkuj">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="10" x2="6" y2="10"/><line x1="21" y1="6" x2="3" y2="6"/><line x1="21" y1="14" x2="3" y2="14"/><line x1="18" y1="18" x2="6" y2="18"/></svg>
        </button>
        <button type="button" class="toolbar-btn" class:active={editor?.isActive({textAlign: 'right'})} onclick={() => setAlign('right')} title="Do prawej">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="21" y1="10" x2="7" y2="10"/><line x1="21" y1="6" x2="3" y2="6"/><line x1="21" y1="14" x2="3" y2="14"/><line x1="21" y1="18" x2="7" y2="18"/></svg>
        </button>

        <div class="w-px h-5 bg-border mx-0.5"></div>

        <button type="button" class="toolbar-btn" class:active={editor?.isActive('link')} onclick={setLink} title="Link">
            &#128279;
        </button>
        <button type="button" class="toolbar-btn" onclick={setHorizontalRule} title="Linia pozioma">
            &mdash;
        </button>
    </div>
    <div bind:this={element}></div>
</div>

<style>
    .toolbar-btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 28px;
        height: 28px;
        border-radius: 4px;
        font-size: 13px;
        color: var(--muted-foreground, #6b7280);
        cursor: pointer;
        border: none;
        background: transparent;
    }
    .toolbar-btn:hover {
        background: var(--accent, #f3f4f6);
        color: var(--foreground, #111827);
    }
    .toolbar-btn.active {
        background: var(--accent, #e5e7eb);
        color: var(--foreground, #111827);
    }
    .highlight-icon {
        background: #fef08a;
        padding: 0 2px;
        border-radius: 2px;
        font-size: 11px;
        font-weight: 600;
    }
    .color-dropdown {
        position: absolute;
        top: 100%;
        left: 0;
        z-index: 50;
        display: flex;
        gap: 4px;
        padding: 6px;
        background: var(--background, #fff);
        border: 1px solid var(--border, #e5e7eb);
        border-radius: 6px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    .color-swatch {
        width: 22px;
        height: 22px;
        border-radius: 4px;
        border: 1px solid var(--border, #d1d5db);
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 10px;
        color: #9ca3af;
    }
    .color-swatch:hover {
        transform: scale(1.15);
    }
</style>
