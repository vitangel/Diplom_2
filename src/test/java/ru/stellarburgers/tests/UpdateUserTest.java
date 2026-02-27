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

@DisplayName("Изменение данных пользователя")
public class UpdateUserTest {

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
    @DisplayName("Изменение данных пользователя с авторизацией (email)")
    @Description("Проверка, что авторизованный пользователь может изменить email.")
    public void testUpdateUserEmailWithAuth() {
        String newEmail = "new_" + user.getEmail();
        User updatedUser = new User(newEmail, user.getPassword(), user.getName());

        Response updateResponse = userSteps.updateUser(updatedUser, accessToken);

        assertEquals(200, updateResponse.statusCode(), "Статус код должен быть 200");
        UserResponse userResponse = updateResponse.as(UserResponse.class);
        assertTrue(userResponse.isSuccess());
        assertEquals(newEmail, userResponse.getUser().getEmail(), "Email должен обновиться");
        assertEquals(user.getName(), userResponse.getUser().getName(), "Имя не должно измениться");
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией (name)")
    @Description("Проверка, что авторизованный пользователь может изменить имя.")
    public void testUpdateUserNameWithAuth() {
        String newName = "NewName";
        User updatedUser = new User(user.getEmail(), user.getPassword(), newName);

        Response updateResponse = userSteps.updateUser(updatedUser, accessToken);

        assertEquals(200, updateResponse.statusCode());
        UserResponse userResponse = updateResponse.as(UserResponse.class);
        assertTrue(userResponse.isSuccess());
        assertEquals(newName, userResponse.getUser().getName(), "Имя должно обновиться");
        assertEquals(user.getEmail(), userResponse.getUser().getEmail(), "Email не должен измениться");
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией (все поля)")
    @Description("Проверка, что авторизованный пользователь может изменить и email, и имя.")
    public void testUpdateUserAllFieldsWithAuth() {
        String newEmail = "new_" + user.getEmail();
        String newName = "NewName";
        User updatedUser = new User(newEmail, user.getPassword(), newName);

        Response updateResponse = userSteps.updateUser(updatedUser, accessToken);

        assertEquals(200, updateResponse.statusCode());
        UserResponse userResponse = updateResponse.as(UserResponse.class);
        assertTrue(userResponse.isSuccess());
        assertEquals(newEmail, userResponse.getUser().getEmail());
        assertEquals(newName, userResponse.getUser().getName());
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Попытка изменить данные без токена. Ожидаем код 401 и сообщение об ошибке.")
    public void testUpdateUserWithoutAuth() {
        // Создаём пользователя с новыми данными
        String newEmail = "new_" + user.getEmail();
        User updatedUser = new User(newEmail, user.getPassword(), user.getName());

        // Вызываем updateUser без токена (передаём null или пустую строку)
        Response updateResponse = userSteps.updateUser(updatedUser, null);

        assertEquals(401, updateResponse.statusCode(), "Статус код должен быть 401");
        String errorMessage = updateResponse.jsonPath().getString("message");
        assertEquals("You should be authorised", errorMessage);
    }
}