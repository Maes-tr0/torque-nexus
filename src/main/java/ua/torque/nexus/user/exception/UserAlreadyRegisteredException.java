package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class UserAlreadyRegisteredException extends ApplicationException {
    public UserAlreadyRegisteredException(String message) {
        super(ExceptionType.USER_ALREADY_REGISTERED, message);
    }
}
