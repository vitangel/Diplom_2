package ru.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.stellarburgers.client.UserClient;
import ru.stellarburgers.model.User;
import ru.stellarburgers.model.UserCredentials;
import ru.stellarburgers.model.UserResponse;

import static org.junit.jupiter.api.Assertions.*;

public class UserSteps {

    private UserClient userClient = new UserClient();

    @Step("Создание уникального пользователя с email: {user.email}, password: {user.password}, name: {user.name}")
    public Response createUser(User user) {
        return userClient.createUser(user);
    }

    @Step("Авторизация пользователя с email: {credentials.email}")
    public Response loginUser(UserCredentials credentials) {
        return userClient.loginUser(credentials);
    }

    @Step("Получение данных пользователя с токеном: {token}")
    public Response getUser(String token) {
        return userClient.getUser(token);
    }

    @Step("Обновление данных пользователя (email: {user.email}, name: {user.name}) с токеном: {token}")
    public Response updateUser(User user, String token) {
        return userClient.updateUser(user, token);
    }

    @Step("Удаление пользователя с токеном: {token}")
    public Response deleteUser(String token) {
        return userClient.deleteUser(token);
    }

    @Step("Проверка успешного ответа при создании пользователя")
    public void assertUserCreatedSuccessfully(Response response, User expectedUser) {
        assertEquals(200, response.statusCode());
        UserResponse userResponse = response.as(UserResponse.class);
        assertTrue(userResponse.isSuccess());
        assertNotNull(userResponse.getAccessToken());
        assertNotNull(userResponse.getRefreshToken());
        assertEquals(expectedUser.getEmail(), userResponse.getUser().getEmail());
        assertEquals(expectedUser.getName(), userResponse.getUser().getName());
    }

    @Step("Проверка ответа с ошибкой (код {expectedStatusCode}, сообщение содержит '{expectedMessage}')")
    public void assertErrorResponse(Response response, int expectedStatusCode, String expectedMessage) {
        assertEquals(expectedStatusCode, response.statusCode());
        String responseBody = response.asString();
        assertTrue(responseBody.contains(expectedMessage));
    }
}