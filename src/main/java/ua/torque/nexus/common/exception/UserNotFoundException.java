package ua.torque.nexus.common.exception;

import java.util.Map;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public UserNotFoundException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public UserNotFoundException(ExceptionType type, String message) {
        super(type, message);
    }

    public UserNotFoundException(ExceptionType type) {
        super(type);
    }
}
