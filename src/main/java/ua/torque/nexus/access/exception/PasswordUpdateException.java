package ua.torque.nexus.access.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class PasswordUpdateException extends ApplicationException {
    public PasswordUpdateException(String message, Map<String, Object> details) {
        super(ExceptionType.PASSWORD_UPDATE_FAILED, message, details);
    }
}
