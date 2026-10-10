package ru.praktikum.scooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

class OrderCreateTest {
    private final OrderClient client = new OrderClient();
    private Integer track;

    static Object[][] colors() {
        return new Object[][] {
                {"BLACK", Arrays.asList("BLACK")},
                {"GREY", Arrays.asList("GREY")},
                {"Оба цвета", Arrays.asList("BLACK", "GREY")},
                {"Пустой массив", Collections.emptyList()},
                {"Без цвета", null}
        };
    }

    @AfterEach
    @Step("Отменить заказ после теста")
    void tearDown() {
        if (track != null) {
            client.cancel(track)
                    .then().statusCode(200).body("ok", equalTo(true));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("colors")
    @DisplayName("Создание заказа с разными цветами")
    @Step("Проверить создание заказа: {name}")
    void createOrder(String name, List<String> colors) {
        Map<String, Object> order = TestData.order(colors);
        Response response = client.create(order);
        track = response.jsonPath().get("track");

        response.then().statusCode(201)
                .body("track", instanceOf(Integer.class))
                .body("track", greaterThan(0));
    }
}
