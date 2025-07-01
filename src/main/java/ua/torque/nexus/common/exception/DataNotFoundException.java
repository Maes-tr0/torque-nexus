package ua.torque.nexus.common.exception;

import java.util.Map;

public class DataNotFoundException extends ApplicationException {

    public DataNotFoundException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public DataNotFoundException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public DataNotFoundException(ExceptionType type, String message) {
        super(type, message);
    }

    public DataNotFoundException(ExceptionType type) {
        super(type);
    }
}
