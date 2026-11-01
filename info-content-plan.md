# Treść Info: ścisły edytor i wygląd z Figmy (plan)

Cel: treść Info wygląda w aplikacji jak w Figmie (strona Drafts → „Info Content”, ramki `Dark / Info / 5–18`),
a edytor w panelu pozwala tylko na style z projektu, w tym na tabele. Tryb jasny i ciemny działa bez
dodatkowej pracy redaktora.

## Zasady

1. **HTML nie niesie kolorów ani rozmiarów.** Zapisujemy tagi i kilka klas, motyw dobiera wygląd.
2. **Jeden zestaw dozwolonych elementów** jest egzekwowany w trzech miejscach: schemat edytora, sanitizer w backendzie, renderer na mobilce.
3. **Edytor zostaje w Tiptapie** (bez forka i bez zmiany biblioteki), ze zwężonym schematem.

## Dozwolone elementy (kontrakt)

| Element HTML | Wygląd (tryb ciemny, z Figmy) |
|---|---|
| `<h2>` | 20px / 500, biały (H3 w Figmie) |
| `<h3>` | 16px / 500, biały (podtytuły „1. Doceń…”, do potwierdzenia) |
| `<p>` | 14px / 300, interlinia 24 |
| `<p class="accent">` | 16px / 500, `#FCC573` (linia miejsca/adresu) |
| `<strong>` | 14px / 500 |
| `<em>`, `<a href>` | kursywa, link (kolor akcentu) |
| `<ul>`, `<ol>`, `<li>` | lista z wcięciem |
| `<table>`, `<thead>`, `<tbody>`, `<tr>`, `<th>`, `<td>` | promień 12, tło `#3C3E40`, padding 1 i odstęp 1 (ramki), komórki `#292B2D`, padding 12, `th` 14px / 500 |
| `<td class="accent">` | tekst `#FCC573` (kolumna z godzinami) |

Poza listą wszystko jest wycinane: `style`, `font`, `span`, kolory, zakreślacz, wyrównanie, podkreślenie, przekreślenie.
Wartości dla trybu jasnego trzeba jeszcze odczytać z ramek `Light / Info / …` w Figmie (krok 0).

## Kroki

### 0. Uzupełnienie specyfikacji (krótko)
- Odczytać z Figmy wartości trybu jasnego dla wszystkich elementów z tabeli i potwierdzić style `h3` / podtytułów.
- Wynik wpisać do tabeli powyżej. Bez tego nie startujemy z CSS.

### 1. Tokeny i CSS treści (panel)
- `forms-ui/src/lib/infoContent.css`: klasa `.info-content` ze zmiennymi (`--ic-text`, `--ic-accent`, `--ic-table-border`, `--ic-table-cell`…), wariant jasny i `.dark`.
- Ta sama klasa stylizuje edytor i podgląd, żeby to, co widać w edytorze, było tym, co dostanie telefon.
- Fonty: Ubuntu 300/500, tak jak w aplikacji.

### 2. Edytor (panel)
`forms-ui/src/lib/components/TiptapEditor.svelte`:
- usunąć `Color`, `TextStyle`, `Highlight`, `TextAlign`, `Underline`, strike, blockquote, hr oraz kolory z toolbara,
- StarterKit skonfigurować: nagłówki tylko poziomy 2 i 3, bez kodu i bloków kodu,
- dodać `@tiptap/extension-table` (+ row, header, cell) z własnymi atrybutami: tylko `class="accent"` na komórce; bez `colwidth` i `style`,
- własne rozszerzenie `accent`: wariant akapitu (`p.accent`) i komórki,
- toolbar: akapit / H2 / H3, pogrubienie, kursywa, link, listy, akcent, tabela (wstaw, dodaj/usuń wiersz i kolumnę, usuń tabelę),
- przełącznik podglądu **jasny / ciemny** nad edytorem (niezależny od motywu panelu), oparty o klasę `.dark` na kontenerze podglądu,
- wklejanie: ProseMirror samo odrzuci niedozwolone znaczniki, więc wklejka z Google Docs się oczyści.

### 3. Backend: sanitizer i migracja
- Zależność `org.jsoup:jsoup`. `InfoContentSanitizer` z `Safelist` o dokładnie powyższym zestawie (dozwolone atrybuty: `href` w `a`, `class` tylko o wartości `accent`).
- Wywołanie w `InfoService.addInfo` / `updateInfo` (dla Info, w tym dla jedynego Info miasta regionalnego).
- Testy (controller-client): wycina `style` i `span`, zostawia tabelę z `accent`, normalizuje `<b>` do `<strong>`, odrzuca `<script>`.
- Nowy changeset `022-sanitize-info-text` (nie edytujemy 019–021): Kotlin `CustomTaskChange` jak w 020, przepuszcza `info_text` istniejących infów przez sanitizer. Test migracji jak `BackfillInfoStyleChangeTest`.

### 4. Mobilka
`OdysejaMobile`, `InfoDetailScreen`:
- Zostajemy przy `flutter_widget_from_html_core`: `InfoContent` używa `customStylesBuilder` (tagi i klasy → tokeny z `theme.t` i kolory zależne od motywu) oraz `customWidgetBuilder` dla tabeli. Zmiana pakietu okazała się niepotrzebna.
- Style inline ze starej treści są ignorowane po stronie aplikacji (`stripInlineStyles`), więc nieoczyszczony HTML nie psuje trybu ciemnego.
- Tabela: własny widget `InfoTable` (zaokrąglony kontener, ramki 1px, komórki rysowane z treści inline; krótkie kolumny przylegają do treści, reszta dzieli miejsce).
- Testy widgetów (jasny i ciemny): treść, kolory z motywu, ignorowanie starych stylów, zawijanie tabeli (`test/presentation/info_content_test.dart`).

### 5. Weryfikacja
- Panel: ręcznie wkleić treść z Google Docs, sprawdzić czyszczenie, tabelę i podgląd jasny/ciemny.
- Backend: pełny `./gradlew cleanTest test` na JDK 17.
- Mobilka: symulator iPhone, oba tryby, ta sama treść co w panelu; porównanie ze zrzutami z Figmy.

## Kolejność i zależności
0 → 1 → 2 mogą iść razem w panelu; 3 niezależnie od 1–2; 4 zależy od zamrożonego kontraktu (krok 0).
Wdrożenie: najpierw backend (sanitizer + migracja), potem panel, na końcu aplikacja. Stara aplikacja dostaje
oczyszczony HTML, który dla niej nadal jest poprawny (tagi standardowe), czyli bez złamania wstecznej zgodności.

## Ryzyka
- Tabele na mobilce są największym kawałkiem pracy (zawijanie, szerokie kolumny).
- Oczyszczenie istniejących 22 infów traci ręczne kolory (czarny tekst) i rozmiary; to zamierzone, ale do przejrzenia przed wdrożeniem na produkcję.
- Podtytuły numerowane („1. Doceń…”): jeśli w Figmie to osobny styl, a nie `h3`, trzeba dodać klasę (decyzja w kroku 0).

## Do ustalenia
- Czy „linia akcentowa” ma być klasą bloku (`p.accent`), czy znacznikiem inline (mark)? Rekomendacja: oba w jednej nazwie klasy, ale w edytorze jako jedna opcja „Akcent”.
- Czy robimy w tej samej fali widok „Podgląd jak w aplikacji” w ramce telefonu? Rekomendacja: tak, ale jako osobny, późniejszy krok.
