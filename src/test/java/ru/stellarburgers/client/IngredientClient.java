package ru.stellarburgers.client;

import io.restassured.response.Response;

public class IngredientClient extends RestClient {
    private static final String INGREDIENTS_PATH = "/api/ingredients";

    public Response getIngredients() {
        return getBaseSpec()
                .when()
                .get(INGREDIENTS_PATH);
    }
}