Zaimplementuj edytor spontanów i wpisywanie wyników dla spontanów
Edytor:
- nowa zakładka na UI Edytor spontanów dostępna tylko dla ADMIN
- każdy spontan ma nazwę
- spontan może być manualny/słowny
- w przypadku słownego nic wiecej się nie definiuje
- W przypadku manualnego definuje się pola dla spontana dodając kolejne (tak jak w formularzach DT) oraz podaje się mnożnik
- Trzymaj definicje w nowej tabeli tak jak trzymane są dla fomrularzy - json per spontan

Strona z spontanami:
- tak jak dal formularzy najpierw widok z miastami do wyboru
- Potem lista grup (Proble + grupa wiekowa + liga)
- Nie admin widiz nazwę i przypisany spontan
- ADmin może przypisać spontan do danej grupy (dropdown na liście)
- Admin może ustalić liczbę sędziów (3 by default) (teź jako dropdown 1-4)

Po kliknięciu na grupę spontanów:
- Lista drużyn posortowana po godzinie spontana (spontanHour z performance table)
Słowny:
- Dwa inputy per sędzia: liczba odpowiedzi kreatywnych/liczba odpowiedzi pospolitych
Manualny
- Dwa inputy per sędzia: Kreatywność rozwiązania/Współpraca drużyny
- input per zdefiniwane pola w formularzu
- save tylko jak dirty state aktywny
- na backendzie liczenia punktacji:
  - Każda komórka razy zdefiniowany mnożnik
  - Dla słownych suma kreatywnych per sędzia * 5 + suma pospolitych i z tego średnia
  - dla manualnych suma Kreatywność rozwiązania/Współpraca drużyny podzielona przez liczbę sędziów
  - zapisz wynik tak jak to jest zrobione w formularzach DT
  - Zwróć an frontend wynik

Generalnie staraj się powtórzyć patterny do edycji formularzy jak w przyapdk uformualrzy DT
Dododaj testy dla każdego scenariusza w ten sposób jak testy są teraz zrobione czyli używajac controller clienta
