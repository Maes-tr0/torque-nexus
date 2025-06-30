package ua.torque.nexus.common.exception;

import java.util.Map;

public class InvalidInputException extends ApplicationException {

    public InvalidInputException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public InvalidInputException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public InvalidInputException(ExceptionType type, String message) {
        super(type, message);
    }

    public InvalidInputException(ExceptionType type) {
        super(type);
    }
}
