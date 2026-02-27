package ru.stellarburgers.tests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.stellarburgers.model.User;
import ru.stellarburgers.model.UserCredentials;
import ru.stellarburgers.model.UserGenerator;
import ru.stellarburgers.model.UserResponse;
import ru.stellarburgers.steps.UserSteps;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Авторизация пользователя")
public class LoginUserTest {

    private UserSteps userSteps = new UserSteps();
    private User user;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        // Создаём уникального пользователя
        user = UserGenerator.getRandomUser();
        Response createResponse = userSteps.createUser(user);
        assertEquals(200, createResponse.statusCode());
        UserResponse userResponse = createResponse.as(UserResponse.class);
        accessToken = userResponse.getAccessToken();
    }

    @AfterEach
    public void tearDown() {
        // Удаляем пользователя после теста
        if (accessToken != null) {
            Response deleteResponse = userSteps.deleteUser(accessToken);
            deleteResponse.then().statusCode(202);
        }
    }

    @Test
    @DisplayName("Авторизация под существующим пользователем")
    @Description("Проверка успешной авторизации с корректными email и password. Ожидаем код 200 и наличие токенов.")
    public void testLoginExistingUser() {
        UserCredentials credentials = UserCredentials.from(user);
        Response loginResponse = userSteps.loginUser(credentials);

        assertEquals(200, loginResponse.statusCode(), "Статус код должен быть 200");

        UserResponse userResponse = loginResponse.as(UserResponse.class);
        assertTrue(userResponse.isSuccess(), "Поле success должно быть true");
        assertNotNull(userResponse.getAccessToken(), "AccessToken не должен быть null");
        assertNotNull(userResponse.getRefreshToken(), "RefreshToken не должен быть null");
        assertEquals(user.getEmail(), userResponse.getUser().getEmail(), "Email должен совпадать");
        assertEquals(user.getName(), userResponse.getUser().getName(), "Имя должно совпадать");
    }

    @Test
    @DisplayName("Авторизация с неверными учётными данными")
    @Description("Попытка авторизации с неправильным паролем. Ожидаем код 401 и сообщение об ошибке.")
    public void testLoginWithInvalidCredentials() {
        UserCredentials invalidCredentials = new UserCredentials(user.getEmail(), "wrongPassword");
        Response loginResponse = userSteps.loginUser(invalidCredentials);

        assertEquals(401, loginResponse.statusCode(), "Статус код должен быть 401");
        String errorMessage = loginResponse.jsonPath().getString("message");
        assertEquals("email or password are incorrect", errorMessage);
    }
}