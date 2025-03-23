package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class UserAlreadyRegisteredException extends ApplicationException {
    public UserAlreadyRegisteredException(String message) {
        super(ExceptionType.USER_ALREADY_REGISTERED, message);
    }
    public UserAlreadyRegisteredException(String message, Map<String, Object> details) {
        super(ExceptionType.USER_ALREADY_REGISTERED, message, details);
    }
}
