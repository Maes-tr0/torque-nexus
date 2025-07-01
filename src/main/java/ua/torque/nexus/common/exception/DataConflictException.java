package ua.torque.nexus.common.exception;

import java.util.Map;

public class DataConflictException extends ApplicationException {

    public DataConflictException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public DataConflictException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public DataConflictException(ExceptionType type, String message) {
        super(type, message);
    }

    public DataConflictException(ExceptionType type) {
        super(type);
    }
}
