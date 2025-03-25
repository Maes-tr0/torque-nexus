package ua.torque.nexus.feature.token.email.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class TokenExpiredException extends ApplicationException {
    public TokenExpiredException(String message) {
        super(ExceptionType.TOKEN_EXPIRED, message);
    }
    public TokenExpiredException(String message, Map<String, Object> details) {
        super(ExceptionType.TOKEN_EXPIRED, message, details);
    }
}
