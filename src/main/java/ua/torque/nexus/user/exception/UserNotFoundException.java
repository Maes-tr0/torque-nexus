package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String message) {
        super(ExceptionType.USER_NOT_FOUND, message);
    }
}
