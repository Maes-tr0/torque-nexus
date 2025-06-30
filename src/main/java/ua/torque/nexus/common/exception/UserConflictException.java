package ua.torque.nexus.common.exception;

import java.util.Map;

public class UserConflictException extends ApplicationException {

    public UserConflictException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public UserConflictException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public UserConflictException(ExceptionType type, String message) {
        super(type, message);
    }

    public UserConflictException(ExceptionType type) {
        super(type);
    }
}
