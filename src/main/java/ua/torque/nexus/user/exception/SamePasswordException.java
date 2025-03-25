package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class SamePasswordException extends ApplicationException {
    public SamePasswordException(String message) {
        super(ExceptionType.SAME_PASSWORD, message);
    }
}
