package ru.praktikum.scooter;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Map;

import static org.hamcrest.Matchers.*;

class CourierLoginTest {
    private final CourierClient client = new CourierClient();
    private Map<String, Object> courier;

    @BeforeEach
    @Step("Создать курьера перед тестом")
    void setUp() {
        courier = TestData.courier();
        client.create(courier)
                .then().statusCode(201).body("ok", equalTo(true));
    }

    @AfterEach
    @Step("Очистить данные курьера")
    void tearDown() {
        client.delete(courier);
    }

    @Test
    @DisplayName("Курьер может войти в систему")
    @Step("Проверить вход с правильными данными")
    void loginCourier() {
        client.login(TestData.credentials(courier))
                .then().statusCode(200)
                .body("id", instanceOf(Integer.class))
                .body("id", greaterThan(0));
    }

    @ParameterizedTest(name = "Неверное поле {0}")
    @ValueSource(strings = {"login", "password"})
    @DisplayName("Вход с неправильным логином или паролем")
    @Step("Проверить вход с неправильным полем {field}")
    void loginWithWrongField(String field) {
        Map<String, Object> request = TestData.credentials(courier);
        request.put(field, TestData.courier().get(field));

        client.login(request)
                .then().statusCode(404).body("message", equalTo(TestData.NOT_FOUND_ERROR));
    }

    @Test
    @DisplayName("Несуществующий курьер не может войти")
    @Step("Проверить вход несуществующего курьера")
    void loginNonexistentCourier() {
        Map<String, Object> request = TestData.credentials(TestData.courier());

        client.login(request)
                .then().statusCode(404).body("message", equalTo(TestData.NOT_FOUND_ERROR));
    }

    @ParameterizedTest(name = "Без поля {0}")
    @ValueSource(strings = {"login", "password"})
    @DisplayName("Вход без обязательного поля")
    @Step("Проверить вход без поля {field}")
    void loginWithoutRequiredField(String field) {
        Map<String, Object> request = TestData.credentials(courier);
        request.remove(field);

        client.login(request)
                .then().statusCode(400).body("message", equalTo(TestData.LOGIN_ERROR));
    }

    @ParameterizedTest(name = "Поле {0}, значение {1}")
    @CsvSource(value = {"login,''", "password,''", "login,NULL", "password,NULL"}, nullValues = "NULL")
    @DisplayName("Вход с пустым логином или паролем")
    @Step("Проверить вход с пустым полем {field}")
    void loginWithEmptyField(String field, String value) {
        Map<String, Object> request = TestData.credentials(courier);
        request.put(field, value);

        client.login(request)
                .then().statusCode(400).body("message", equalTo(TestData.LOGIN_ERROR));
    }
}
