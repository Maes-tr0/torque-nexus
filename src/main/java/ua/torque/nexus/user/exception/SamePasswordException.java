package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class SamePasswordException extends ApplicationException {
    public SamePasswordException() {
        super(ExceptionType.SAME_PASSWORD, "New password must be different from the old password");
    }
}
