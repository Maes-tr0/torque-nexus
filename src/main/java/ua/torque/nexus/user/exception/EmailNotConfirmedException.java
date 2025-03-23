package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class EmailNotConfirmedException extends ApplicationException {
    public EmailNotConfirmedException(String message) {
        super(ExceptionType.EMAIL_NOT_CONFIRMED, message);
    }
    public EmailNotConfirmedException(String message, Map<String, Object> details) {
        super(ExceptionType.EMAIL_NOT_CONFIRMED, message, details);
    }
}
