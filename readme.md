# Changelog


## M1 - 15.12.2023

<ul>
<li>Inicjalizacja projektu</li>
<li>Podpięcie testowej bazy H2</li>
<li>Zrealizowanie architektury pokazanej na zajęciach z pewnymi zmianami</li>
<img src="arch.png" alt="architecture">
<li>Dodanie przyjmowania pojedynczego zdjęcia w requeście</li>
<li>Dodanie zapisywania zdjęć w bazie</li>
<li>Dodanie GET endpointów do pobierania zdjęć z bazy</li>
<li>Dodanie przyjmowania wielu zdjęć w requeście</li>
<li>Dodanie tworzenia miniatur</li>
<li>Dodanie reaktywnego przetwarzania wysłanych zdjęć</li>
</ul>

## Najbliższe plany
<ul>
<li>Zmiana bazy H2 na docelową bazę aplikacji</li>
<li>Stworzenie cache'u po stronie bazy, aby uodpornić aplikację na crash'e</li>
<li>Frontend i komunikacja</li>
</ul>

## Endpunkty

### 1. Testowy Endpunkt

- **Metoda:** `GET`
- **Ścieżka:** `/photos/test`
- **Opis:** Testowy endpoint zwracający napis "Testing" w odpowiedzi.

### 2. Upload pojedynczego pliku

- **Metoda:** `POST`
- **Ścieżka:** `/photos/upload`
- **Parametry:** `file` (MultipartFile) - przesyłany plik
- **Opis:** Endpoint umożliwiający przesyłanie pojedynczego pliku. Tworzy bilet w bazie danych, przetwarza plik za pomocą `HandlingService` i zwraca odpowiedź, czy plik został pomyślnie przesłany.

### 3. Upload wielu plików

- **Metoda:** `POST`
- **Ścieżka:** `/photos/upload/bulk`
- **Parametry:** `files` (List\<MultipartFile\>) - lista przesyłanych plików
- **Opis:** Endpoint umożliwiający przesyłanie wielu plików. Podobnie jak poprzedni endpoint, tworzy bilet w bazie danych, przetwarza pliki za pomocą `HandlingService` i zwraca odpowiedź.

### 4. Pobieranie zdjęcia po identyfikatorze

- **Metoda:** `GET`
- **Ścieżka:** `/photos/{imageId}`
- **Parametry:** `imageId` (Long) - identyfikator zdjęcia, `imageSize` (opcjonalny, domyślnie "original") - rozmiar zdjęcia
- **Opis:** Endpoint umożliwiający pobieranie zdjęcia o określonym identyfikatorze. Zwraca odpowiedź zawierającą obraz w formie bajtów.

### 5. Pobieranie zdjęć związanych z biletem

- **Metoda:** `GET`
- **Ścieżka:** `/photos/tickets/{ticketID}`
- **Parametry:** `ticketID` (String) - identyfikator biletu, `imageSize` (opcjonalny, domyślnie "original") - rozmiar zdjęcia
- **Opis:** Endpoint umożliwiający pobieranie zdjęć związanych z danym biletem. Zwraca odpowiedź zawierającą listę obrazów w formie bajtów.

### 6. Pobieranie wszystkich zdjęć

- **Metoda:** `GET`
- **Ścieżka:** `/photos/photos`
- **Parametry:** `imageSize` (opcjonalny, domyślnie "original") - rozmiar zdjęcia
- **Opis:** Endpoint umożliwiający pobieranie wszystkich zdjęć. Zwraca odpowiedź zawierającą listę obrazów w formie bajtów.
