## `Статус сборки` [![Build status](https://ci.appveyor.com/api/projects/status/bm64h5m2momjgw7r?svg=true)](https://ci.appveyor.com/project/Lognestix/aqa-exercise-2-4-2)
# Репортинг (AQA_Exercise_2.4-2)
## Домашнее задание по курсу "Автоматизированное тестирование"
## Тема: «2.4. BDD», задание №2: «BDD»
- На базе инструмента Cucumber, при использовании Page Object's, реализованы следующие Steps:
	- Пусть пользователь залогинен на странице "http://localhost:9999" с именем "vasya" и паролем "qwerty123" с введенным проверочным кодом 'из смс' "12345";
	- Когда пользователь на свою первую карту c id "92df3f1c-a033-48e6-8390-206f6b1f56c0" с карты с номером "5559 0000 0000 0002" переведет 5000 рублей;
	- Тогда баланс его первойй карты c id "92df3f1c-a033-48e6-8390-206f6b1f56c0" из списка на главной странице должен стать 15000 рублей.
### Предварительные требования
- На компьютере пользователя должна быть установлена:
	- Intellij IDEA
### Установка и запуск
1. Склонировать проект на свой компьютер
	- открыть терминал
	- ввести команду 
		```
		git clone https://github.com/Lognestix/AQA_Exercise_2.4-2
		```
1. Открыть склонированный проект в Intellij IDEA
1. В Intellij IDEA перейти во вкладку Terminal (Alt+F12) и запустить SUT командой
	```
	java -jar artifacts/app-ibank-build-for-testers.jar
	```
1. Запустить BDD-тест можно двумя путями:
	1. В проекте Intellij IDEA перейти к файлу RunCucumberTest.java, находящемуся в /src/test/java:
		- осуществить запуск теста.
	1. В проекте Intellij IDEA перейти к файлу Positive.feature, находящемуся в /src/test/resources/features:
		- осуществить запуск теста.