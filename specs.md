# Edytor Formularzy

Specyfikacja jak mają działać forularze w Odysejapce czyli edycja formularzy, wspiywanie danych dla konkretnej drużyny,
zarządzanie użytkwonikami.

## Role

- Administrator - dostęp do wszystkich funkcji
- Kapitan - Dostęp do edycji formularzy i dostęp do wszystkich formularzy drużynowych z danego problemu
- Sędzia - Dostęp do formularzy drużynowych z danej sceny

## Strony

- Edytor formularzy - możliwość wyboru dla którego problemu edytujemy formularz (Dostep tylko da)
- Formularze drużynowe - lista drużyn z formularzem dla danego problemu dla danej drużyny
- Zarządzanie użytkownikami - możliwość zarządzania użytkownikami w Odysejapce
- Globalny dashboard - możliwość przeglądania ogólnych statystyk i informacji o systemie (Ilość wypełnonych formularzy
  etc)

## Formularze

Zwaliduj najpierw co już jest zaimplementowane

Problem
Ustalanie liczby sędziów w zależności od miasta
przypisanie sędziów do danego zestawu
dwa warianty sędziów (w dziwnym przypadku może być więcej)
Sędzie stylu to osobny sędzia
Rodzaje typów wierszy
średnia z przedziału
Element nie wystąpił - wyzerowanie + blokowanie komórki
Grupy punktacji - suma średnich - widoczenie w AOC nie widoczne w AOS
Wartości dyskretne (obiektywna) muszą być takie same dla wszystkich sędziów
Definiowanie grup punktacji
W przypadku problemu 4 dodawanie wagi balsy
Subiektywna - dyskretne wartości bez wymagania zgodności wobec sędziów
Wymagany komentarz jeśli nie ma maksa w obiektywnej punktacji
Wymagany komentarz gdy wyzerowana komórka
Styl
Sędziów stylu też może być 1 albo 2
Wpisanie nazw dwóch kategori - wciągnie ich do AOS
tylko subiektywne
Elemnt może nie wystąpić - zerowanie komórek
Karne
Skala przewinienia - subiektywna skala
obiektywne - konkretna wartość ujamna
Czas - ma się samo przeliczać
Karne za czas liczą się tylko w 3 i 5
Dodawanie komentarzy do punktów karnych
Wszystkie komentarzy muszą się przenieść do AOS - jeżeli się pojawiły
W balsie podgrupy karnych
Anomalia
Niepełna punktacja obiektywna
0 w punktacji subiektywnej - nie pojawił się jakiś element (dt, styl)
jakiekolwiek punkty karne
Wyświetlanie anomali w ZSP
Póki anomalia nie zatwierdzona przez kapitana - nie wolno drukować
Zatwierdzanie anomali w ZSP z komentarzem

Balsa
Dodawanie kolejnych obciążeń
Pierwsze pole 0, 2.5kg
reszta wybór z różnych obciążęń
Możliwość wyzerowania punktów za balsę w karnych
Wypisanie w AOS obciążęń


