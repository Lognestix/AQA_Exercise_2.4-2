#language:ru

Функциональность: Позитивный сценарий перевода денег с одной своей карты на другую свою карту

  # пример теста с одним набором параметров
  Сценарий: : Перевода денег с одной своей карты на другую свою карту (позитивный)
    Пусть пользователь залогинен на странице "http://localhost:9999" с именем "vasya" и паролем "qwerty123" с введенным проверочным кодом 'из смс' "12345"
    Когда пользователь на свою первую карту c id "92df3f1c-a033-48e6-8390-206f6b1f56c0" с карты с номером "5559 0000 0000 0002" переведет 5000 рублей
    Тогда баланс его первойй карты c id "92df3f1c-a033-48e6-8390-206f6b1f56c0" из списка на главной странице должен стать 15000 рублей