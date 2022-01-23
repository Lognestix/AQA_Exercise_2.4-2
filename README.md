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

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
  private final SelenideElement heading = $("[data-test-id=dashboard]");
  private final SelenideElement title = $("h1.heading");
  private final String balanceStart = "баланс:";
  private final String balanceFinish = "р.";

  public DashboardPage() {
    heading.shouldBe(visible);
    title.shouldHave(text("Ваши карты"));
  }

  private int extractBalance(String cardInfo) {
    //Вырезается нужная часть строки:
    var value = cardInfo.substring
            //Начальная позиция (исключительно) плюс смещение:
                    (cardInfo.indexOf(balanceStart) + balanceStart.length(),
                            //Конечная позиция (включительно):
                            cardInfo.indexOf(balanceFinish))
            //Обрезка начального и конечного пробелов:
            .trim();
    return Integer.parseInt(value);
  }

  public int getCardBalance(String cardId) {
    return extractBalance($("[data-test-id='" + cardId + "']").getText());
  }

  public ReplenishmentPage transfer(String cardId) {
    $("[data-test-id='" + cardId + "'] [data-test-id=action-deposit]").click();
    return new ReplenishmentPage();
  }
}
```
```Java
package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ReplenishmentPage {
    private final SelenideElement heading = $("[data-test-id=dashboard]");
    private final SelenideElement title = $("h1.heading");
    private final SelenideElement amountField = $("[data-test-id=amount] input");
    private final SelenideElement fromField = $("[data-test-id=from] input");
    private final SelenideElement transferButton = $("[data-test-id=action-transfer]");
    private final SelenideElement errorNotification = $("[data-test-id=error-notification]");
    private final SelenideElement cancelButton = $("[data-test-id=action-cancel]");

    public ReplenishmentPage() {
        heading.shouldBe(visible);
        title.shouldHave(text("Пополнение карты"));
    }

    private void fieldClearing() {
        amountField.sendKeys(Keys.CONTROL + "a");
        amountField.sendKeys(Keys.DELETE);
        fromField.sendKeys(Keys.CONTROL + "a");
        fromField.sendKeys(Keys.DELETE);
    }

    public DashboardPage transferBetweenOwnCards(int amount, String numberCard) {
        fieldClearing();
        amountField.setValue(String.valueOf(amount));
        fromField.setValue(numberCard);
        transferButton.click();
        if(errorNotification.is(visible)) {
            cancelButton.click();
        }
        return new DashboardPage();
    }
}
```
## Запуск авто-тестов BDD (Cucumber) находящиихся в этом репозитории.
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
import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;
import ru.netology.page.DashboardPage;
import ru.netology.page.ReplenishmentPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateSteps {
    private static LoginPage loginPage;
    private static VerificationPage verificationPage;
    private static DashboardPage dashboardPage;
    private static ReplenishmentPage replenishmentPage;

    @Пусть("пользователь залогинен на странице {string} с именем {string} " +
            "и паролем {string} с введенным проверочным кодом 'из смс' {string}")
    public void userAuthorization(String url, String login, String password, String verificationCode) {
        loginPage = Selenide.open(url, LoginPage.class);
        verificationPage = loginPage.validLogin(login, password);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Когда("пользователь на свою первую карту c id {string} с карты с номером {string} переведет {int} рублей")
    public void moneyTransferBetweenOwnCards(String idCard, String numberCard, int amount) {
        replenishmentPage = dashboardPage.transfer(idCard);
        replenishmentPage.transferBetweenOwnCards(amount, numberCard);
    }

    @Тогда("баланс его первойй карты c id {string} из списка на главной странице должен стать {int} рублей")
    public void verifyCodeIsInvalid(String idCard, int expectedBalance) {
        assertEquals(expectedBalance, dashboardPage.getCardBalance(idCard));
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