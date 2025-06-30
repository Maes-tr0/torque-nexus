package ua.torque.nexus.common.exception;

import java.util.Map;

public class AuthenticationException extends ApplicationException {

    public AuthenticationException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public AuthenticationException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public AuthenticationException(ExceptionType type, String message) {
        super(type, message);
    }

    public AuthenticationException(ExceptionType type) {
        super(type);
    }
}
