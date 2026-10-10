package ru.praktikum.scooter;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private RequestSpecification request() {
        return given()
                .baseUri(TestData.setting("base.url"))
                .basePath("/api/v1")
                .contentType(ContentType.JSON)
                .config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", TestData.number("connect.timeout.ms"))
                        .setParam("http.socket.timeout", TestData.number("read.timeout.ms"))))
                .filter(new AllureRestAssured());
    }

    @Step("Создать заказ")
    public Response create(Map<String, Object> order) {
        return request().body(order).post("/orders");
    }

    @Step("Получить список заказов")
    public Response list() {
        return request().queryParam("limit", TestData.number("orders.limit"))
                .queryParam("page", TestData.number("orders.page")).get("/orders");
    }

    @Step("Отменить заказ с track={track}")
    public Response cancel(int track) {
        return request().queryParam("track", track).put("/orders/cancel");
    }
}
