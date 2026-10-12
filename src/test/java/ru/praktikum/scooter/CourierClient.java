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
import static org.hamcrest.Matchers.*;

public class CourierClient {
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

    @Step("Создать курьера")
    public Response create(Map<String, Object> courier) {
        return request().body(courier).post("/courier");
    }

    @Step("Войти под курьером")
    public Response login(Map<String, Object> credentials) {
        return request().body(credentials).post("/courier/login");
    }

    @Step("Удалить курьера после теста")
    public void delete(Map<String, Object> courier) {
        Response response = login(TestData.credentials(courier));
        if (response.statusCode() == 404) {
            response.then().body("message", equalTo(TestData.NOT_FOUND_ERROR));
            return;
        }
        response.then().statusCode(200).body("id", instanceOf(Integer.class)).body("id", greaterThan(0));
        int id = response.jsonPath().getInt("id");
        request().pathParam("id", id).delete("/courier/{id}")
                .then().statusCode(200).body("ok", equalTo(true));
    }
}
