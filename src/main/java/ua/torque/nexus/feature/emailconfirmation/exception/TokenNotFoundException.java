package ua.torque.nexus.feature.emailconfirmation.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class TokenNotFoundException extends ApplicationException {
    public TokenNotFoundException(String message) {
        super(ExceptionType.TOKEN_NOT_FOUND, message);
    }
}
