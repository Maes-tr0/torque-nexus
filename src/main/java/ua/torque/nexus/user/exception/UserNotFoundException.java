package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String message) {
        super(ExceptionType.USER_NOT_FOUND, message);
    }
    public UserNotFoundException(String message, Map<String, Object> details) {
        super(ExceptionType.USER_NOT_FOUND, message, details);
    }
}
