import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';

export function registerLatexThymeleaf(m: typeof monaco) {
    const id = 'latex-th';
    if ((m.languages as any).getLanguages?.().some((l: any) => l.id === id)) return;

    m.languages.register({id});

    m.languages.setLanguageConfiguration(id, {
        comments: {lineComment: '%'},
        brackets: [
            ['{', '}'],
            ['[', ']'],
            ['(', ')']
        ],
        autoClosingPairs: [
            {open: '{', close: '}'},
            {open: '[', close: ']'},
            {open: '(', close: ')'},
            {open: '"', close: '"'},
            {open: '\'', close: '\''},
            {open: '[[', close: ']]'},
            {open: '[#', close: ']'}
        ],
        surroundingPairs: [
            {open: '{', close: '}'},
            {open: '[', close: ']'},
            {open: '(', close: ')'},
            {open: '"', close: '"'},
            {open: '\'', close: '\''}
        ]
    });

    m.languages.setMonarchTokensProvider(id, {
        defaultToken: '',
        tokenPostfix: '.ltxth',
        includeLF: true,

        // quick symbols
        brackets: [
            {open: '{', close: '}', token: 'delimiter.bracket'},
            {open: '[', close: ']', token: 'delimiter.array'},
            {open: '(', close: ')', token: 'delimiter.parenthesis'}
        ],

        tokenizer: {
            root: [
                // Thymeleaf closing block [/]
                [/\[\s*\/\s*\]/, 'th.block.delim'],

                // Thymeleaf inline [[ ... ]]
                [/\[\[/, {token: 'th.inline.delim', next: '@thInline'}],

                // Thymeleaf block [# ... ]
                [/\[\s*#/, {token: 'th.block.delim', next: '@thBlock'}],

                // Comments
                [/%.*$/, 'comment'],

                // \begin{...} \end{...}
                [/\\(?:begin|end)\s*\{[^}]*\}/, 'keyword'],

                // LaTeX command: \command, \@special
                [/\\[a-zA-Z@]+/, 'latex.command'],

                // Escaped characters like \%, \$, \_, \{, \}
                [/\\[^a-zA-Z]/, 'latex.escape'],

                // Math modes: $...$, \(...\), \[...\]
                [/\$\$/, {token: 'delimiter', next: '@mathDisplayDollar'}],
                [/\$/, {token: 'delimiter', next: '@mathInlineDollar'}],
                [/\\\(/, {token: 'delimiter', next: '@mathInlineParen'}],
                [/\\\[/, {token: 'delimiter', next: '@mathDisplayBrack'}],

                // Numbers
                [/\b\d+(\.\d+)?\b/, 'number'],

                // Strings (rare in LaTeX, but safe)
                [/"/, {token: 'string.quote', next: '@string'}],

                // Delimiters
                [/[{}\[\]\(\),;]/, 'delimiter'],
            ],

            // Inline Thymeleaf [[ ... ]]
            thInline: [
                [/\]\]/, {token: 'th.inline.delim', next: '@pop'}],
                [/\$\{[^}]*\}/, 'th.expr'], // ${...}
                [/[a-zA-Z_]\w*(\.[a-zA-Z_]\w*)*/, 'identifier'],
                [/'.*?'/, 'string'],
                [/"(\\.|[^"])*"?/, 'string'],
                [/[?:!=<>+\-*/%]+/, 'operator'],
                [/[\(\)\.,]/, 'delimiter'],
                [/[\s]+/, '']
            ],

            // Block Thymeleaf [# ... ]
            thBlock: [
                [/\]/, {token: 'th.block.delim', next: '@pop'}],
                [/th:[a-zA-Z\-]+/, 'th.attr'],
                [/\$\{[^}]*\}/, 'th.expr'],
                [/"(\\.|[^"])*"?/, 'string'],
                [/'(\\.|[^'])*'?/, 'string'],
                [/[=:]/, 'operator'],
                [/[\w\.\-]+/, 'identifier'],
                [/\s+/, '']
            ],

            // Math states
            mathInlineDollar: [
                [/\\\$/, 'string.math'],                // escaped $
                [/\$/, {token: 'delimiter', next: '@pop'}],
                [/[^$]+/, 'string.math']
            ],
            mathDisplayDollar: [
                [/\\\$\$/, 'string.math'],
                [/\$\$/, {token: 'delimiter', next: '@pop'}],
                [/[^$]+/, 'string.math']
            ],
            mathInlineParen: [
                [/\\\)/, {token: 'delimiter', next: '@pop'}],
                [/[^)]+/, 'string.math']
            ],
            mathDisplayBrack: [
                [/\\\]/, {token: 'delimiter', next: '@pop'}],
                [/[^]+?/, 'string.math']
            ],

            string: [
                [/[^"]+/, 'string'],
                [/"/, {token: 'string.quote', next: '@pop'}]
            ]
        }
    });

    // Themes
    m.editor.defineTheme('latex-th-light', {
        base: 'vs',
        inherit: true,
        rules: [
            {token: 'comment', foreground: '7f7f7f'},
            {token: 'latex.command', foreground: '0000aa', fontStyle: 'bold'},
            {token: 'latex.escape', foreground: '0000aa'},
            {token: 'string', foreground: 'a31515'},
            {token: 'string.math', foreground: '267f99'},
            {token: 'number', foreground: '098658'},
            {token: 'th.inline.delim', foreground: 'a31515', fontStyle: 'bold'},
            {token: 'th.block.delim', foreground: 'a31515', fontStyle: 'bold'},
            {token: 'th.attr', foreground: '795e26', fontStyle: 'bold'},
            {token: 'th.expr', foreground: '267f99'},
            {token: 'identifier', foreground: '2b91af'},
            {token: 'operator', foreground: '000000'},
            {token: 'delimiter', foreground: '000000'},
            {token: 'type', foreground: '267f99'}
        ],
        colors: {}
    });

    m.editor.defineTheme('latex-th-dark', {
        base: 'vs-dark',
        inherit: true,
        rules: [
            {token: 'comment', foreground: '808080'},
            {token: 'latex.command', foreground: '79b8ff', fontStyle: 'bold'},
            {token: 'latex.escape', foreground: '79b8ff'},
            {token: 'string', foreground: 'ce9178'},
            {token: 'string.math', foreground: '4ec9b0'},
            {token: 'number', foreground: 'b5cea8'},
            {token: 'th.inline.delim', foreground: 'd19a66', fontStyle: 'bold'},
            {token: 'th.block.delim', foreground: 'd19a66', fontStyle: 'bold'},
            {token: 'th.attr', foreground: 'e5c07b', fontStyle: 'bold'},
            {token: 'th.expr', foreground: '61afef'},
            {token: 'identifier', foreground: '9cdcfe'},
            {token: 'operator', foreground: 'd4d4d4'},
            {token: 'delimiter', foreground: 'd4d4d4'}
        ],
        colors: {}
    });

    monaco.languages.registerCompletionItemProvider(id, {
        triggerCharacters: ['[', '#', '$', '{'],
        provideCompletionItems(model, position) {
            const word = model.getWordUntilPosition(position);
            const range = new monaco.Range(
                position.lineNumber,
                word.startColumn,
                position.lineNumber,
                word.endColumn
            );

            const suggestions: monaco.languages.CompletionItem[] = [
                {
                    label: '[# th:each]',
                    kind: monaco.languages.CompletionItemKind.Snippet,
                    insertText: '[# th:each="${1:item} : ${2:items}"]\n$0\n[/]',
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                    documentation: 'Thymeleaf block: th:each',
                    range
                },
                {
                    label: '[# th:if]',
                    kind: monaco.languages.CompletionItemKind.Snippet,
                    insertText: '[# th:if="${1:condition}"]\n$0\n[/]',
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                    documentation: 'Thymeleaf block: th:if',
                    range
                },
                {
                    label: '[[...]]',
                    kind: monaco.languages.CompletionItemKind.Snippet,
                    insertText: '[[${$1}]]',
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                    documentation: 'Thymeleaf inline expression',
                    range
                }
            ];

            return {suggestions};
        }
    });
}