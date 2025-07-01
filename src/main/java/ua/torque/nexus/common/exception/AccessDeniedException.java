package ua.torque.nexus.common.exception;

import java.util.Map;

public class AccessDeniedException extends ApplicationException {
    public AccessDeniedException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public AccessDeniedException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public AccessDeniedException(ExceptionType type, String message) {
        super(type, message);
    }

    public AccessDeniedException(ExceptionType type) {
        super(type);
    }
}
