package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class SamePasswordException extends ApplicationException {
    public SamePasswordException(String message) {
        super(ExceptionType.SAME_PASSWORD, message);
    }
    public SamePasswordException(String message, Map<String, Object> details) {
        super(ExceptionType.SAME_PASSWORD, message, details);
    }
}
