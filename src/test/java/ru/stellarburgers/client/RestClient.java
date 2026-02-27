package ru.stellarburgers.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RestClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru";

    protected RequestSpecification getBaseSpec() {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .filter(new AllureRestAssured());
    }

    protected RequestSpecification getAuthSpec(String token) {
        if (token == null || token.isBlank()) {
            return getBaseSpec();
        }
        return getBaseSpec().header("Authorization", token);
    }
}