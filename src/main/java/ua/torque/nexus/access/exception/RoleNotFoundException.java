package ua.torque.nexus.access.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class RoleNotFoundException extends ApplicationException {

    public RoleNotFoundException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }

    public RoleNotFoundException(ExceptionType type, Map<String, Object> details) {
        super(type, details);
    }

    public RoleNotFoundException(ExceptionType type, String message) {
        super(type, message);
    }

    public RoleNotFoundException(ExceptionType type) {
        super(type);
    }
}
