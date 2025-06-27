package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class InvalidCredentialsException extends ApplicationException {
    public InvalidCredentialsException() {
        super(ExceptionType.INVALID_CREDENTIALS, ExceptionType.INVALID_CREDENTIALS.getMessage());
    }
}
