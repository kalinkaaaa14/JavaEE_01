package kma.topic2.junit.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final String login) {
        super("Can't find user by login: " + login);
    }

}
