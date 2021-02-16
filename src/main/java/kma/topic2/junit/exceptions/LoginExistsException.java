package kma.topic2.junit.exceptions;

public class LoginExistsException extends RuntimeException {

    public LoginExistsException(final String login) {
        super(String.format("Login %s already taken", login));
    }

}
