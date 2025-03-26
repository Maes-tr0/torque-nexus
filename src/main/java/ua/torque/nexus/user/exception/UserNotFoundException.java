package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String email) {
        super(ExceptionType.USER_NOT_FOUND, "User not found: " + email);
    }
}
