package ua.torque.nexus.common.exception;

import java.util.Map;

public class AuthorizationException extends ApplicationException {

    public AuthorizationException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public AuthorizationException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public AuthorizationException(ExceptionType type, String message) {
        super(type, message);
    }

    public AuthorizationException(ExceptionType type) {
        super(type);
    }
}
