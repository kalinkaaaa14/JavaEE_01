package kma.topic2.junit.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kma.topic2.junit.exceptions.UserNotFoundException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserRepository {

    private static final Map<String, User> USER_DATABASE = initDatabase();

    public User saveNewUser(final NewUser newUser) {
        log.info("Creating new user: {}", newUser);

        final User user =  User.builder()
            .fullName(newUser.getFullName())
            .login(newUser.getLogin())
            .password(newUser.getPassword())
            .build();

        USER_DATABASE.put(user.getLogin(), user);
        return user;
    }

    public User getUserByLogin(final String login) {
        log.info("Get user by login: {}", login);

        return Optional.ofNullable(USER_DATABASE.get(login))
            .orElseThrow(() -> new UserNotFoundException(login));
    }

    public boolean isLoginExists(final String login) {
        log.info("Check that user with login: {} exists", login);

        return USER_DATABASE.containsKey(login);
    }

    private static Map<String, User> initDatabase() {
        final Map<String, User> database = new HashMap<>();
        database.put("login1", User.builder().login("login1").password("password1").fullName("fullName1").build());
        database.put("login2", User.builder().login("login2").password("password2").fullName("fullName2").build());
        database.put("login3", User.builder().login("login3").password("password3").fullName("fullName3").build());
        return database;
    }

}
