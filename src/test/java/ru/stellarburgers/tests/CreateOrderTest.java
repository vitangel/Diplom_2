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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Создание заказа")
public class CreateOrderTest {

    private UserSteps userSteps = new UserSteps();
    private OrderSteps orderSteps = new OrderSteps();
    private IngredientClient ingredientClient = new IngredientClient();

    private User user;
    private String accessToken;
    private List<String> validIngredientIds;

    @BeforeEach
    public void setUp() {
        // Получаем валидные ID ингредиентов через API
        Response ingredientsResponse = ingredientClient.getIngredients();
        assertEquals(200, ingredientsResponse.statusCode());
        IngredientsResponse ingredients = ingredientsResponse.as(IngredientsResponse.class);
        // Берём первые два ингредиента для заказа
        if (ingredients.getData().size() >= 2) {
            validIngredientIds = Arrays.asList(
                    ingredients.getData().get(0).get_id(),
                    ingredients.getData().get(1).get_id()
            );
        } else {
            // Запасной вариант, если вдруг ингредиентов меньше 2
            validIngredientIds = Arrays.asList("60d3b41abdacab0026a733c6", "609646e4dc916e00276b2870");
        }

        // Создаём пользователя для авторизованных тестов
        user = UserGenerator.getRandomUser();
        Response createResponse = userSteps.createUser(user);
        assertEquals(200, createResponse.statusCode());
        UserResponse userResponse = createResponse.as(UserResponse.class);
        accessToken = userResponse.getAccessToken();
    }

    @AfterEach
    public void tearDown() {
        // Удаляем пользователя после тестов
        if (accessToken != null) {
            Response deleteResponse = userSteps.deleteUser(accessToken);
            deleteResponse.then().statusCode(202);
        }
    }

    // Тесты для авторизованного пользователя

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    @Description("Авторизованный пользователь создаёт заказ с двумя ингредиентами. Ожидаем код 200 и success=true.")
    public void testCreateOrderWithAuthAndValidIngredients() {
        OrderRequest orderRequest = new OrderRequest(validIngredientIds);
        Response response = orderSteps.createOrderWithAuth(orderRequest, accessToken);
        orderSteps.assertOrderCreatedSuccessfully(response);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    @Description("Авторизованный пользователь пытается создать заказ без ингредиентов. Ожидаем код 400.")
    public void testCreateOrderWithAuthWithoutIngredients() {
        OrderRequest orderRequest = new OrderRequest(Collections.emptyList());
        Response response = orderSteps.createOrderWithAuth(orderRequest, accessToken);
        orderSteps.assertOrderWithoutIngredientsError(response);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и неверным хешем ингредиента")
    @Description("Авторизованный пользователь пытается создать заказ с невалидным ID ингредиента. Ожидаем код 500.")
    public void testCreateOrderWithAuthAndInvalidHash() {
        List<String> invalidIngredients = Arrays.asList("invalidHash123", "anotherInvalid");
        OrderRequest orderRequest = new OrderRequest(invalidIngredients);
        Response response = orderSteps.createOrderWithAuth(orderRequest, accessToken);
        orderSteps.assertInvalidIngredientHashError(response);
    }

    // Тесты для неавторизованного пользователя

    @Test
    @DisplayName("Создание заказа без авторизации с валидными ингредиентами")
    @Description("Неавторизованный пользователь создаёт заказ с двумя ингредиентами. Ожидаем код 200 и success=true.")
    public void testCreateOrderWithoutAuthAndValidIngredients() {
        OrderRequest orderRequest = new OrderRequest(validIngredientIds);
        Response response = orderSteps.createOrder(orderRequest);
        orderSteps.assertOrderCreatedSuccessfully(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации без ингредиентов")
    @Description("Неавторизованный пользователь пытается создать заказ без ингредиентов. Ожидаем код 400.")
    public void testCreateOrderWithoutAuthWithoutIngredients() {
        OrderRequest orderRequest = new OrderRequest(Collections.emptyList());
        Response response = orderSteps.createOrder(orderRequest);
        orderSteps.assertOrderWithoutIngredientsError(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хешем ингредиента")
    @Description("Неавторизованный пользователь пытается создать заказ с невалидным ID ингредиента. Ожидаем код 500.")
    public void testCreateOrderWithoutAuthAndInvalidHash() {
        List<String> invalidIngredients = Arrays.asList("invalidHash123", "anotherInvalid");
        OrderRequest orderRequest = new OrderRequest(invalidIngredients);
        Response response = orderSteps.createOrder(orderRequest);
        orderSteps.assertInvalidIngredientHashError(response);
    }
}