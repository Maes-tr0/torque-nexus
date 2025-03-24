package ua.torque.nexus.access.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class UnsupportedRoleTypeException extends ApplicationException {
    public UnsupportedRoleTypeException(String message, Map<String, Object> details) {
        super(ExceptionType.UNSUPPORTED_ROLE_TYPE, message, details);
    }
}
