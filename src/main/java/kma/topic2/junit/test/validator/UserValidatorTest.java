package kma.topic2.junit.test.validator;

import kma.topic2.junit.exceptions.ConstraintViolationException;
import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.repository.UserRepository;
import kma.topic2.junit.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {
    @Mock
    private UserRepository repository;
    @InjectMocks
    private UserValidator validator;


    private static Stream<Arguments> shouldCreateUserSuccessfullyDataProvider() {
        return Stream.of(
                Arguments.of("qwer12", "log1", "Alina Kupchyk"),
                Arguments.of("asdf45", "log2", "Daryna Melnyk"),
                Arguments.of("zxc123", "log3", "Anastasiya Lish"),
                Arguments.of("test123", "log4", "Anna Kost")
        );
    }

    @ParameterizedTest
    @MethodSource("shouldCreateUserSuccessfullyDataProvider")
    void shouldCreateUserSuccessfully(String password, String login, String fullname) {
        validator.validateNewUser(
                NewUser.builder()
                        .password(password)
                        .login(login)
                        .fullName(fullname)
                        .build()
        );
        verify(repository).isLoginExists(login);
    }

    @Test
    void shouldThrowExceptionWhenLoginAlreadyExists(){
        String login = "login";
        when(repository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(true);
        assertThatThrownBy(() -> validator.validateNewUser(
                NewUser.builder()
                        .password("pass")
                        .login(login)
                        .fullName("fullname")
                        .build()
        ))
                .isInstanceOf(LoginExistsException.class)
                .hasMessage("Login "+login+" already taken");
    }

    @ParameterizedTest
    @MethodSource("testPasswordDataProvider")
    void shouldThrowExceptionWhenPasswordInvalid(String password, List<String> errors) {
        assertThatThrownBy(() -> validator.validateNewUser(
                NewUser.builder()
                        .password(password)
                        .fullName("fullname")
                        .login("log123")
                        .build()
        ))
                .isInstanceOfSatisfying(
                        ConstraintViolationException.class,
                        ex -> assertThat(ex.getErrors()).isEqualTo(errors)
                );
    }

    private static Stream<Arguments> testPasswordDataProvider() {
        return Stream.of(
                Arguments.of("padh##",List.of("Password doesn't match regex")),
                Arguments.of("pa", List.of("Password has invalid size")),
                Arguments.of("pasreushiuhff", List.of("Password has invalid size")),
                Arguments.of("pasreushiuhff@#", List.of("Password has invalid size","Password doesn't match regex")),
                Arguments.of("a#", List.of("Password has invalid size", "Password doesn't match regex"))
        );
    }
}
