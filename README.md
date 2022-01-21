## `Статус сборки` [![Build status](https://ci.appveyor.com/api/projects/status/bm64h5m2momjgw7r?svg=true)](https://ci.appveyor.com/project/Lognestix/aqa-exercise-2-4-2)
## В build.gradle добавленна поддержка JUnit-Jupiter, Junit-Vintage-Engine, Cucumber-Java, Cucumber-Junit, Simple, Selenide и headless-режим, Lombok.
```gradle
plugins {
    id 'java'
}

group 'ru.netology'
version '1.0-SNAPSHOT'

sourceCompatibility = 11

//Кодировка файлов (если используется русский язык в файлах)
compileJava.options.encoding = "UTF-8"
compileTestJava.options.encoding = "UTF-8"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly 'org.projectlombok:lombok:1.18.22'
    annotationProcessor 'org.projectlombok:lombok:1.18.22'

    testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'
    testImplementation 'org.junit.vintage:junit-vintage-engine:5.8.2'
    testImplementation 'com.codeborne:selenide:6.2.0'
    testImplementation 'io.cucumber:cucumber-java:7.2.3'
    testImplementation 'io.cucumber:cucumber-junit:7.2.3'
    testImplementation 'org.slf4j:slf4j-simple:1.7.33'

    testCompileOnly 'org.projectlombok:lombok:1.18.22'
    testAnnotationProcessor 'org.projectlombok:lombok:1.18.22'
}

test {
    useJUnitPlatform()
    //В тестах, при вызове `gradlew test -Dselenide.headless=true` будет передаватся этот параметр в JVM (где его подтянет Selenide)
    systemProperty 'selenide.headless', System.getProperty('selenide.headless')
}
```
## Код Java для оптимизации авто-тестов.
```Java
package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
  private final SelenideElement loginField = $("[data-test-id=login] input");
  private final SelenideElement passwordField = $("[data-test-id=password] input");
  private final SelenideElement loginButton = $("[data-test-id=action-login]");

  public VerificationPage validLogin(String login, String password) {
    loginField.setValue(login);
    passwordField.setValue(password);
    loginButton.click();
    return new VerificationPage();
  }
}
```
```Java
package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
  private final SelenideElement codeField = $("[data-test-id=code] input");
  private final SelenideElement verifyButton = $("[data-test-id=action-verify]");

  public VerificationPage() {
    codeField.shouldBe(visible);
  }

  public DashboardPage validVerify(String verificationCode) {
    codeField.setValue(verificationCode);
    verifyButton.click();
    return new DashboardPage();
  }
}
```
```Java
package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.data.NotFoundException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
  private final SelenideElement heading = $("[data-test-id=dashboard]");
  private static final ElementsCollection cards = $$(".list__item");
  private static final String balanceStart = "баланс:";
  private static final String balanceFinish = "р.";
  private static final SelenideElement amountField = $("[data-test-id=amount] input");
  private static final SelenideElement fromField = $("[data-test-id=from] input");
  private static final SelenideElement actionTransferButton = $("[data-test-id=action-transfer]");

  public DashboardPage() {
    heading.shouldBe(visible);
  }

  public static int getCardBalance(String id) {
    for (SelenideElement card : cards) {
      var attributeValue = card.find("[data-test-id]").attr("data-test-id");
      assert attributeValue != null;
      if (attributeValue.equals(id)) {
        return extractBalance(card.text());
      }
    }
    throw new NotFoundException(
            "Card with id: " + id + " not found");
  }

    private static int extractBalance(String cardInfo) {
    var value = cardInfo.substring    //Вырезается нужная часть строки
            (cardInfo.indexOf(balanceStart) + balanceStart.length(),    //Начальная позиция (исключительно) плюс смещение
                    cardInfo.indexOf(balanceFinish))    //Конечная позиция (включительно)
            .trim();    //Обрезка начального и конечного пробелов
    return Integer.parseInt(value);
  }

  public static void transferBetweenOwnCards(String idCard, String numberCard, int amount) {
    for (SelenideElement card : cards) {
      var attributeValue = card.find("[data-test-id]").attr("data-test-id");
      assert attributeValue != null;
      if (attributeValue.equals(idCard)) {
        card.find("[data-test-id=action-deposit]").click();
        amountField.sendKeys(Keys.CONTROL + "a");
        amountField.sendKeys(Keys.DELETE);
        amountField.setValue(String.valueOf(amount));
        fromField.sendKeys(Keys.CONTROL + "a");
        fromField.sendKeys(Keys.DELETE);
        fromField.setValue(numberCard);
        actionTransferButton.click();
        return;
      }
    }
  }
}
```
```Java
package ru.netology.data;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) {
        super(msg);
    }
}
```
## Запуск авто-тестов Cucumber находящиихся в этом репозитории.
```Java
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "summary"},
        features = {"src/test/resources/features"},
        glue = {"ru.netology.steps"})
public class RunCucumberTest {
}
```
## Шаги в Java коде.
```Java
package ru.netology.steps;

import com.codeborne.selenide.Selenide;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Пусть;
import io.cucumber.java.ru.Тогда;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateSteps {
    private static LoginPage loginPage;
    private static DashboardPage dashboardPage;
    private static VerificationPage verificationPage;

    @Пусть("пользователь залогинен на странице {string} с именем {string} " +
            "и паролем {string} с введенным проверочным кодом 'из смс' {string}")
    public void userAuthorization(String url, String login, String password, String verificationCode) {
        loginPage = Selenide.open(url, LoginPage.class);
        verificationPage = loginPage.validLogin(login, password);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Когда("пользователь на свою первую карту c id {string} с карты с номером {string} переведет {int} рублей")
    public void moneyTransferBetweenOwnCards(String idCard, String numberCard, int amount) {
        DashboardPage.transferBetweenOwnCards(idCard, numberCard, amount);
    }

    @Тогда("баланс его первойй карты c id {string} из списка на главной странице должен стать {int} рублей")
    public void verifyCodeIsInvalid(String id, int finalBalance) {
        assertEquals(finalBalance, DashboardPage.getCardBalance(id));
    }
}
```
## Сценарий.
```Gherkin
#language:ru

Функциональность: Позитивный сценарий перевода денег с одной своей карты на другую свою карту

  Сценарий: : Перевода денег с одной своей карты на другую свою карту (позитивный)
    Пусть пользователь залогинен на странице "http://localhost:9999" с именем "vasya" и паролем "qwerty123" с введенным проверочным кодом 'из смс' "12345"
    Когда пользователь на свою первую карту c id "92df3f1c-a033-48e6-8390-206f6b1f56c0" с карты с номером "5559 0000 0000 0002" переведет 5000 рублей
    Тогда баланс его первойй карты c id "92df3f1c-a033-48e6-8390-206f6b1f56c0" из списка на главной странице должен стать 15000 рублей
```