package kma.topic2.junit.test.service;

import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.exceptions.UserNotFoundException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.model.User;
import kma.topic2.junit.repository.UserRepository;
import kma.topic2.junit.service.UserService;
import kma.topic2.junit.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @SpyBean
    private UserRepository userRepository;
    @SpyBean
    private UserValidator userValidator;

    @Test
    void shouldCreateUserSuccessfullyTest(){
        String login = "newlogtest";
        userService.createNewUser(
                NewUser.builder()
                        .password("pass")
                        .login(login)
                        .fullName("fullname")
                        .build()
        );

        verify(userValidator).validateNewUser(any());
        assertThat(userRepository.getUserByLogin(login)).isEqualTo(
            User.builder()
                    .password("pass")
                    .login(login)
                    .fullName("fullname")
                    .build()
        );
    }

    @Test
    void shouldNotFoundUserTest() {
        final String login = "notExistingLogin";
        assertThatThrownBy(() -> userService.getUserByLogin(login))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Can't find user by login: "+login);
    }

    @Test
    void getUserByLoginTest() {
        NewUser newUser = NewUser.builder()
                              .password("pass")
                              .login("newlog")
                              .fullName("fullname")
                              .build();
        userService.createNewUser(newUser);

        assertThat(userService.getUserByLogin(newUser.getLogin())).isEqualTo(
                User.builder()
                        .password(newUser.getPassword())
                        .login(newUser.getLogin())
                        .fullName(newUser.getFullName())
                        .build()
        );
        verify(userRepository).getUserByLogin(anyString());
    }

    @ParameterizedTest
    @MethodSource("shouldThrowExceptionWhenLoginAlreadyExistsDataProvider")
    void shouldThrowExceptionWhenLoginAlreadyExistsTest(String password, String login, String fullName) {
        userService.createNewUser(
                NewUser.builder()
                        .password(password)
                        .login(login)
                        .fullName(fullName)
                        .build()
        );

        assertThatThrownBy(() -> userService.createNewUser(
                NewUser.builder()
                        .fullName(fullName)
                        .login(login)
                        .password(password)
                        .build()
        ))
                .isInstanceOf(LoginExistsException.class);
    }

    private static Stream<Arguments> shouldThrowExceptionWhenLoginAlreadyExistsDataProvider() {
        return Stream.of(Arguments.of("pass", "test","fullname"));
    }
}
