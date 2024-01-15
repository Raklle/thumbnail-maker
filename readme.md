Paginacja chyba działa na endpoincie /photos, trzeba podać:
-size, rozmiar strony
-page, nr strony
-imgSize, small/medium/large

Testowałem też foldery, teraz jest w bazie zapisana ścieżka, domyślnie jest pusty string.
jest opcja żeby dodać folder i wtedy dodaje puste zdjęcie z state na ImageState.DIRECTORY,
istnienie folderu nie jest potrzebne żeby zapisać/odczytać zdjęcie przy podaniu ścieżki

dodawanie zdjęć działało jak dałem kilka zdjęć i jeden parametr path np. "/test"
nie testowałem co sie dzieje jak sie zrobi coś innego

odczytywanie z konkretnego folderu wymaga dodania parametru path do endpointów które już były