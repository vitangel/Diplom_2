package ru.stellarburgers.model;
import java.util.UUID;

public class UserGenerator {
    public static User getRandomUser() {
        String email = UUID.randomUUID().toString() + "@yandex.ru";
        String password = UUID.randomUUID().toString().substring(0, 10);
        String name = UUID.randomUUID().toString().substring(0, 8);
        return new User(email, password, name);
    }
}