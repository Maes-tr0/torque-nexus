package ua.torque.nexus.common.exception;

import java.util.Map;

public class TestApplicationException extends ApplicationException {
    public TestApplicationException(ExceptionType type, String message, Map<String, Object> details) {
        super(type, message, details);
    }
}

