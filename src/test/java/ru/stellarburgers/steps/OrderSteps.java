package ru.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.stellarburgers.client.OrderClient;
import ru.stellarburgers.model.OrderRequest;
import ru.stellarburgers.model.OrderResponse;

import static org.junit.jupiter.api.Assertions.*;

public class OrderSteps {

    private OrderClient orderClient = new OrderClient();

    @Step("Создание заказа с ингредиентами: {ingredients} (без авторизации)")
    public Response createOrder(OrderRequest orderRequest) {
        return orderClient.createOrder(orderRequest);
    }

    @Step("Создание заказа с ингредиентами: {ingredients} (с авторизацией, токен: {token})")
    public Response createOrderWithAuth(OrderRequest orderRequest, String token) {
        return orderClient.createOrderWithAuth(orderRequest, token);
    }

    @Step("Получение заказов пользователя (с авторизацией, токен: {token})")
    public Response getUserOrders(String token) {
        return orderClient.getUserOrders(token);
    }

    @Step("Проверка успешного создания заказа (код 200, success=true, номер заказа не null)")
    public void assertOrderCreatedSuccessfully(Response response) {
        assertEquals(200, response.statusCode());
        OrderResponse orderResponse = response.as(OrderResponse.class);
        assertTrue(orderResponse.isSuccess());
        assertNotNull(orderResponse.getOrder());
        assertTrue(orderResponse.getOrder().getNumber() > 0);
    }

    @Step("Проверка ответа при создании заказа без ингредиентов (код 400, сообщение об ошибке)")
    public void assertOrderWithoutIngredientsError(Response response) {
        assertEquals(400, response.statusCode());
        String responseBody = response.asString();
        assertTrue(responseBody.contains("Ingredient ids must be provided"));
    }

    @Step("Проверка ответа при неверном хеше ингредиента (код 500)")
    public void assertInvalidIngredientHashError(Response response) {
        assertEquals(500, response.statusCode());
    }

    @Step("Проверка ответа при получении заказов без авторизации (код 401)")
    public void assertUnauthorizedError(Response response) {
        assertEquals(401, response.statusCode());
        String responseBody = response.asString();
        assertTrue(responseBody.contains("You should be authorised"));
    }
}