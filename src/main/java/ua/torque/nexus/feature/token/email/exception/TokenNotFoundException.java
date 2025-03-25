package ua.torque.nexus.feature.token.email.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class TokenNotFoundException extends ApplicationException {
    public TokenNotFoundException(String message) {
        super(ExceptionType.TOKEN_NOT_FOUND, message);
    }
    public TokenNotFoundException(String message, Map<String, Object> details) {
        super(ExceptionType.TOKEN_NOT_FOUND, message, details);
    }
}
