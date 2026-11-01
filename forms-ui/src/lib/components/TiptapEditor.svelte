<script lang="ts">
    import {onMount, onDestroy} from 'svelte';
    import {Editor, Extension} from '@tiptap/core';
    import StarterKit from '@tiptap/starter-kit';
    import {Table, TableRow, TableHeader, TableCell} from '@tiptap/extension-table';
    import '$lib/infoContent.css';

    let {
        value = $bindable(''),
        theme = 'light',
    }: { value: string; theme?: 'light' | 'dark' } = $props();

    let element: HTMLDivElement;
    let editor: Editor | undefined;
    // Bumped on every editor transaction so the toolbar state below is recomputed.
    let tick = $state(0);

    function is(name: string, attrs?: Record<string, unknown>): boolean {
        tick;
        return editor?.isActive(name, attrs) ?? false;
    }

    /**
     * The "accent" variant of a paragraph or a table cell (`<p class="accent">`, `<td class="accent">`).
     * It is the only visual option besides the structure; the app and the backend sanitizer know the same class.
     */
    const Accent = Extension.create({
        name: 'accent',
        addGlobalAttributes() {
            return [{
                types: ['paragraph', 'tableCell'],
                attributes: {
                    accent: {
                        default: false,
                        parseHTML: (el: HTMLElement) => el.classList.contains('accent'),
                        renderHTML: (attrs: Record<string, unknown>) => (attrs.accent ? {class: 'accent'} : {}),
                    },
                },
            }];
        },
    });

    // Cells are never merged or resized, so their span/width attributes are not part of the content.
    // The defaults must stay valid (1, not null), otherwise the table normalisation drops the table.
    const noSpans = {
        colspan: {default: 1, rendered: false},
        rowspan: {default: 1, rendered: false},
        colwidth: {default: null, rendered: false},
    };
    // No <colgroup> and no inline width in the stored HTML: the table is laid out by the viewer.
    const ContentTable = Table.extend({
        renderHTML() {
            return ['table', ['tbody', 0]];
        },
    });
    const Cell = TableCell.extend({
        addAttributes() {
            return {...this.parent?.(), ...noSpans};
        },
    });
    const HeaderCell = TableHeader.extend({
        addAttributes() {
            return {...this.parent?.(), ...noSpans};
        },
    });

    // Only what the content may contain: headings 2-3, paragraphs, bold, italic, links, lists, tables.
    const schema = StarterKit.configure({
        heading: {levels: [2, 3]},
        underline: false,
        strike: false,
        code: false,
        codeBlock: false,
        blockquote: false,
        horizontalRule: false,
        link: {openOnClick: false, HTMLAttributes: {rel: null, target: null}},
    });

    onMount(() => {
        editor = new Editor({
            element,
            extensions: [
                schema,
                Accent,
                ContentTable.configure({resizable: false}),
                TableRow,
                HeaderCell,
                Cell,
            ],
            content: value,
            onTransaction: () => {
                tick++;
            },
            onUpdate: ({editor: e}) => {
                value = e.getHTML();
            },
        });
    });

    onDestroy(() => {
        editor?.destroy();
    });

    let inTable = $derived(is('table'));
    let accentActive = $derived(is('paragraph', {accent: true}) || is('tableCell', {accent: true}));

    function toggleAccent() {
        if (!editor) return;
        const chain = editor.chain().focus();
        if (editor.isActive('tableCell') || editor.isActive('tableHeader')) {
            chain.setCellAttribute('accent', !editor.isActive('tableCell', {accent: true})).run();
        } else {
            chain.updateAttributes('paragraph', {accent: !editor.isActive('paragraph', {accent: true})}).run();
        }
    }

    function setBlock(kind: 'paragraph' | 2 | 3) {
        const chain = editor?.chain().focus();
        if (kind === 'paragraph') chain?.setParagraph().run();
        else chain?.setHeading({level: kind}).run();
    }

    function setLink() {
        const url = prompt('URL:');
        if (url) editor?.chain().focus().setLink({href: url}).run();
        else editor?.chain().focus().unsetLink().run();
    }

    function insertTable() {
        editor?.chain().focus().insertTable({rows: 3, cols: 2, withHeaderRow: true}).run();
    }
</script>

<div class="rounded-md border border-input bg-background overflow-hidden">
    <div class="flex flex-wrap gap-0.5 border-b bg-muted/30 px-1 py-1 items-center">
        <button type="button" class="toolbar-btn" class:active={is('paragraph') && !accentActive}
                onclick={() => setBlock('paragraph')} title="Akapit">P
        </button>
        <button type="button" class="toolbar-btn" class:active={is('heading', {level: 2})}
                onclick={() => setBlock(2)} title="Nagłówek">H2
        </button>
        <button type="button" class="toolbar-btn" class:active={is('heading', {level: 3})}
                onclick={() => setBlock(3)} title="Podtytuł">H3
        </button>
        <button type="button" class="toolbar-btn accent-btn" class:active={accentActive} onclick={toggleAccent}
                title="Akcent (linia miejsca, godziny w tabeli)">A
        </button>

        <div class="w-px h-5 bg-border mx-0.5"></div>

        <button type="button" class="toolbar-btn" class:active={is('bold')}
                onclick={() => editor?.chain().focus().toggleBold().run()} title="Pogrubienie"><b>B</b></button>
        <button type="button" class="toolbar-btn" class:active={is('italic')}
                onclick={() => editor?.chain().focus().toggleItalic().run()} title="Kursywa"><i>I</i></button>
        <button type="button" class="toolbar-btn" class:active={is('link')} onclick={setLink}
                title="Link">&#128279;</button>

        <div class="w-px h-5 bg-border mx-0.5"></div>

        <button type="button" class="toolbar-btn" class:active={is('bulletList')}
                onclick={() => editor?.chain().focus().toggleBulletList().run()} title="Lista">&#8226;</button>
        <button type="button" class="toolbar-btn" class:active={is('orderedList')}
                onclick={() => editor?.chain().focus().toggleOrderedList().run()} title="Lista numerowana">1.
        </button>

        <div class="w-px h-5 bg-border mx-0.5"></div>

        <button type="button" class="toolbar-btn" class:active={inTable} onclick={insertTable} disabled={inTable}
                title="Wstaw tabelę" aria-label="Wstaw tabelę">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="4" width="18" height="16" rx="2"/>
                <path d="M3 10h18M10 4v16"/>
            </svg>
        </button>
        {#if inTable}
            <button type="button" class="toolbar-text-btn" onclick={() => editor?.chain().focus().addRowAfter().run()}>+ wiersz</button>
            <button type="button" class="toolbar-text-btn" onclick={() => editor?.chain().focus().deleteRow().run()}>− wiersz</button>
            <button type="button" class="toolbar-text-btn" onclick={() => editor?.chain().focus().addColumnAfter().run()}>+ kolumna</button>
            <button type="button" class="toolbar-text-btn" onclick={() => editor?.chain().focus().deleteColumn().run()}>− kolumna</button>
            <button type="button" class="toolbar-text-btn danger" onclick={() => editor?.chain().focus().deleteTable().run()}>Usuń tabelę</button>
        {/if}
    </div>
    <div class="info-content" data-theme={theme} bind:this={element}></div>
</div>

<style>
    .toolbar-btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        min-width: 28px;
        height: 28px;
        padding: 0 4px;
        border-radius: 4px;
        font-size: 13px;
        color: var(--muted-foreground, #6b7280);
        cursor: pointer;
        border: none;
        background: transparent;
    }

    .toolbar-btn:hover:not(:disabled) {
        background: var(--accent, #f3f4f6);
        color: var(--foreground, #111827);
    }

    .toolbar-btn.active {
        background: var(--accent, #e5e7eb);
        color: var(--foreground, #111827);
    }

    .toolbar-btn:disabled {
        opacity: 0.6;
        cursor: default;
    }

    .accent-btn {
        color: #e37900;
        font-weight: 600;
    }

    .toolbar-text-btn {
        height: 28px;
        padding: 0 6px;
        border-radius: 4px;
        border: none;
        background: transparent;
        color: var(--muted-foreground, #6b7280);
        font-size: 12px;
        cursor: pointer;
    }

    .toolbar-text-btn:hover {
        background: var(--accent, #f3f4f6);
        color: var(--foreground, #111827);
    }

    .toolbar-text-btn.danger {
        color: #dc2626;
    }
</style>
