package ua.torque.nexus.common.exception;

import java.util.Map;

public class OperationFailedException extends ApplicationException {

    public OperationFailedException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public OperationFailedException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public OperationFailedException(ExceptionType type, String message) {
        super(type, message);
    }

    public OperationFailedException(ExceptionType type) {
        super(type);
    }
}
