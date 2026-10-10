package ru.praktikum.scooter;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

class CourierCreateTest {
    private final CourierClient client = new CourierClient();
    private Map<String, Object> courier;

    @BeforeEach
    @Step("Подготовить курьера для теста")
    void setUp() {
        courier = TestData.courier();
    }

    @AfterEach
    @Step("Очистить данные курьера")
    void tearDown() {
        client.delete(courier);
    }

    @Test
    @DisplayName("Курьера можно создать")
    @Step("Проверить создание курьера")
    void createCourier() {
        client.create(courier)
                .then().statusCode(201).body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Step("Проверить повторное создание курьера")
    void createDuplicateCourier() {
        client.create(courier)
                .then().statusCode(201).body("ok", equalTo(true));

        client.create(courier)
                .then().statusCode(409).body("message", containsString(TestData.DUPLICATE_ERROR));
    }

    @Test
    @DisplayName("Нельзя использовать занятый логин")
    @Step("Проверить создание с занятым логином")
    void createCourierWithExistingLogin() {
        client.create(courier)
                .then().statusCode(201).body("ok", equalTo(true));

        Map<String, Object> anotherCourier = TestData.courier();
        anotherCourier.put("login", courier.get("login"));

        client.create(anotherCourier)
                .then().statusCode(409).body("message", containsString(TestData.DUPLICATE_ERROR));
    }

    @ParameterizedTest(name = "Без поля {0}")
    @ValueSource(strings = {"login", "password", "firstName"})
    @DisplayName("Создание без обязательного поля")
    @Step("Проверить создание без поля {field}")
    void createCourierWithoutRequiredField(String field) {
        Map<String, Object> request = new HashMap<>(courier);
        request.remove(field);

        client.create(request)
                .then().statusCode(400).body("message", equalTo(TestData.CREATE_ERROR));
    }

    @ParameterizedTest(name = "Поле {0}, значение {1}")
    @CsvSource(value = {"login,''", "password,''", "login,NULL", "password,NULL"}, nullValues = "NULL")
    @DisplayName("Создание с пустым логином или паролем")
    @Step("Проверить создание с пустым полем {field}")
    void createCourierWithEmptyField(String field, String value) {
        Map<String, Object> request = new HashMap<>(courier);
        request.put(field, value);

        client.create(request)
                .then().statusCode(400).body("message", equalTo(TestData.CREATE_ERROR));
    }
}
