package ru.praktikum.scooter;

import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;

class OrderListTest {
    private final OrderClient client = new OrderClient();

    @Test
    @DisplayName("В ответе есть список заказов")
    @Step("Проверить список заказов")
    void getOrders() {
        client.list()
                .then().statusCode(200).body("orders", instanceOf(List.class));
    }
}
