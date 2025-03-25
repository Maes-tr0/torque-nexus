package ua.torque.nexus.feature.token.email.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class EmailAlreadyConfirmedException extends ApplicationException {
    public EmailAlreadyConfirmedException(String message) {
        super(ExceptionType.EMAIL_ALREADY_CONFIRMED, message);
    }
    public EmailAlreadyConfirmedException(String message, Map<String, Object> details) {
        super(ExceptionType.EMAIL_ALREADY_CONFIRMED, message, details);
    }
}
