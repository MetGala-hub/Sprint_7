package ru.praktikum.scooter;

import io.qameta.allure.Step;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class TestData {
    private static final Properties SETTINGS = new Properties();

    public static final String CREATE_ERROR = "Недостаточно данных для создания учетной записи";
    public static final String LOGIN_ERROR = "Недостаточно данных для входа";
    public static final String DUPLICATE_ERROR = "Этот логин уже используется";
    public static final String NOT_FOUND_ERROR = "Учетная запись не найдена";

    static {
        try (InputStream stream = TestData.class.getResourceAsStream("/api.properties")) {
            if (stream == null) {
                throw new IllegalStateException("Не найден файл api.properties");
            }
            SETTINGS.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать настройки", e);
        }
    }

    public static String setting(String name) {
        return System.getProperty(name, SETTINGS.getProperty(name));
    }

    public static int number(String name) {
        return Integer.parseInt(setting(name));
    }

    @Step("Подготовить данные курьера")
    public static Map<String, Object> courier() {
        Map<String, Object> courier = new HashMap<>();
        courier.put("login", "s7_" + UUID.randomUUID());
        courier.put("password", UUID.randomUUID().toString());
        courier.put("firstName", setting("courier.firstName"));
        return courier;
    }

    @Step("Подготовить логин и пароль")
    public static Map<String, Object> credentials(Map<String, Object> courier) {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("login", courier.get("login"));
        credentials.put("password", courier.get("password"));
        return credentials;
    }

    @Step("Подготовить данные заказа")
    public static Map<String, Object> order(List<String> colors) {
        Map<String, Object> order = new HashMap<>();
        order.put("firstName", setting("order.firstName"));
        order.put("lastName", setting("order.lastName"));
        order.put("address", setting("order.address"));
        order.put("metroStation", setting("order.metroStation"));
        order.put("phone", setting("order.phone"));
        order.put("rentTime", number("order.rentTime"));
        order.put("deliveryDate", LocalDate.now().plusDays(number("order.delivery.offset.days")).toString());
        order.put("comment", setting("order.comment"));
        if (colors != null) {
            order.put("color", colors);
        }
        return order;
    }
}
