package ru.stellarburgers.client;

import io.restassured.response.Response;
import ru.stellarburgers.model.OrderRequest;

public class OrderClient extends RestClient {

    private static final String ORDERS_PATH = "/api/orders";

    // Создание заказа (без авторизации)
    public Response createOrder(OrderRequest orderRequest) {
        return getBaseSpec()
                .body(orderRequest)
                .when()
                .post(ORDERS_PATH);
    }

    // Создание заказа с авторизацией
    public Response createOrderWithAuth(OrderRequest orderRequest, String token) {
        return getAuthSpec(token)
                .body(orderRequest)
                .when()
                .post(ORDERS_PATH);
    }

    // Получение заказов конкретного пользователя (с авторизацией)
    public Response getUserOrders(String token) {
        return getAuthSpec(token)
                .when()
                .get(ORDERS_PATH);
    }
}