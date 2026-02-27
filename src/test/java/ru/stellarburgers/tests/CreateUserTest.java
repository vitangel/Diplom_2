package ru.stellarburgers.tests;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.stellarburgers.model.User;
import ru.stellarburgers.model.UserGenerator;
import ru.stellarburgers.model.UserResponse;
import ru.stellarburgers.steps.UserSteps;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Создание пользователя")
public class CreateUserTest {

    private UserSteps userSteps = new UserSteps();
    private User user;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        // Генерируем уникального пользователя перед каждым тестом
        user = UserGenerator.getRandomUser();
    }

    @AfterEach
    public void tearDown() {
        // Если был создан пользователь, удаляем его
        if (accessToken != null) {
            Response deleteResponse = userSteps.deleteUser(accessToken);
            deleteResponse.then().statusCode(202);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка, что можно создать пользователя с уникальными email, password, name. Ожидаем код 200 и наличие токенов.")
    public void testCreateUniqueUser() {
        Response response = userSteps.createUser(user);

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        UserResponse userResponse = response.as(UserResponse.class);
        assertTrue(userResponse.isSuccess(), "Поле success должно быть true");
        assertNotNull(userResponse.getAccessToken(), "AccessToken не должен быть null");
        assertNotNull(userResponse.getRefreshToken(), "RefreshToken не должен быть null");
        assertEquals(user.getEmail(), userResponse.getUser().getEmail(), "Email должен совпадать");
        assertEquals(user.getName(), userResponse.getUser().getName(), "Имя должно совпадать");

        // Сохраняем токен для удаления
        accessToken = userResponse.getAccessToken();
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Попытка создать пользователя с уже существующими данными. Ожидаем код 403 и сообщение об ошибке.")
    public void testCreateExistingUser() {
        // Создаём пользователя
        Response createResponse = userSteps.createUser(user);
        assertEquals(200, createResponse.statusCode());
        UserResponse userResponse = createResponse.as(UserResponse.class);
        accessToken = userResponse.getAccessToken();

        // Пытаемся создать такого же пользователя ещё раз
        Response duplicateResponse = userSteps.createUser(user);

        assertEquals(403, duplicateResponse.statusCode(), "Статус код должен быть 403");
        String errorMessage = duplicateResponse.jsonPath().getString("message");
        assertEquals("User already exists", errorMessage, "Сообщение об ошибке должно быть 'User already exists'");
    }

    @Test
    @DisplayName("Создание пользователя без одного из обязательных полей")
    @Description("Проверка, что при отсутствии email, password или name возвращается код 403 и сообщение об ошибке.")
    public void testCreateUserWithoutRequiredField() {
        // Создаём пользователя без email
        User userWithoutEmail = new User(null, user.getPassword(), user.getName());
        Response response = userSteps.createUser(userWithoutEmail);

        assertEquals(403, response.statusCode(), "Статус код должен быть 403");
        String errorMessage = response.jsonPath().getString("message");
        assertEquals("Email, password and name are required fields", errorMessage);

     }
}