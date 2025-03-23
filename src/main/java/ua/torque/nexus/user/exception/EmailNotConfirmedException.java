package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class EmailNotConfirmedException extends ApplicationException {
    public EmailNotConfirmedException(String message) {
        super(ExceptionType.EMAIL_NOT_CONFIRMED, message);
    }
}
