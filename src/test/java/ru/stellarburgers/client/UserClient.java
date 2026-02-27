package ru.stellarburgers.client;

import io.restassured.response.Response;
import ru.stellarburgers.model.User;
import ru.stellarburgers.model.UserCredentials;

public class UserClient extends RestClient {

    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";

    // Регистрация нового пользователя
    public Response createUser(User user) {
        return getBaseSpec()
                .body(user)
                .when()
                .post(REGISTER_PATH);
    }

    // Авторизация (логин)
    public Response loginUser(UserCredentials credentials) {
        return getBaseSpec()
                .body(credentials)
                .when()
                .post(LOGIN_PATH);
    }

    // Получение данных пользователя
    public Response getUser(String token) {
        return getAuthSpec(token)
                .when()
                .get(USER_PATH);
    }

    // Обновление данных пользователя
    public Response updateUser(User user, String token) {
        return getAuthSpec(token)
                .body(user)
                .when()
                .patch(USER_PATH);
    }

    // Удаление пользователя
    public Response deleteUser(String token) {
        return getAuthSpec(token)
                .when()
                .delete(USER_PATH);
    }
}