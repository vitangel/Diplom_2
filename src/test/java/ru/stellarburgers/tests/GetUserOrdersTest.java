package ru.stellarburgers.tests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.stellarburgers.client.IngredientClient;
import ru.stellarburgers.model.*;
import ru.stellarburgers.steps.OrderSteps;
import ru.stellarburgers.steps.UserSteps;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Получение заказов конкретного пользователя")
public class GetUserOrdersTest {

    private UserSteps userSteps = new UserSteps();
    private OrderSteps orderSteps = new OrderSteps();
    private IngredientClient ingredientClient = new IngredientClient();

    private User user;
    private String accessToken;
    private List<String> validIngredientIds;

    @BeforeEach
    public void setUp() {
        // Получаем валидные ID ингредиентов
        Response ingredientsResponse = ingredientClient.getIngredients();
        assertEquals(200, ingredientsResponse.statusCode());
        IngredientsResponse ingredients = ingredientsResponse.as(IngredientsResponse.class);
        validIngredientIds = Arrays.asList(
                ingredients.getData().get(0).get_id(),
                ingredients.getData().get(1).get_id()
        );

        // Создаём пользователя
        user = UserGenerator.getRandomUser();
        Response createResponse = userSteps.createUser(user);
        assertEquals(200, createResponse.statusCode());
        UserResponse userResponse = createResponse.as(UserResponse.class);
        accessToken = userResponse.getAccessToken();

        // Создаём заказ для этого пользователя
        OrderRequest orderRequest = new OrderRequest(validIngredientIds);
        Response orderResponse = orderSteps.createOrderWithAuth(orderRequest, accessToken);
        orderSteps.assertOrderCreatedSuccessfully(orderResponse);
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            Response deleteResponse = userSteps.deleteUser(accessToken);
            deleteResponse.then().statusCode(202);
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    @Description("Авторизованный пользователь запрашивает свои заказы. Ожидаем код 200 и список заказов (не пустой).")
    public void testGetUserOrdersWithAuth() {
        Response response = orderSteps.getUserOrders(accessToken);

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        OrdersResponse ordersResponse = response.as(OrdersResponse.class);
        assertTrue(ordersResponse.isSuccess(), "Поле success должно быть true");
        assertNotNull(ordersResponse.getOrders(), "Список заказов не должен быть null");
        assertFalse(ordersResponse.getOrders().isEmpty(), "Список заказов не должен быть пустым");

    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    @Description("Неавторизованный пользователь пытается получить заказы. Ожидаем код 401.")
    public void testGetUserOrdersWithoutAuth() {
        Response response = orderSteps.getUserOrders(null);
        orderSteps.assertUnauthorizedError(response);
    }
}