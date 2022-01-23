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