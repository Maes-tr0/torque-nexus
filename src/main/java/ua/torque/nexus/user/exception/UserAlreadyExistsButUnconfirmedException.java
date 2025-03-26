package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class UserAlreadyExistsButUnconfirmedException extends ApplicationException {
    public UserAlreadyExistsButUnconfirmedException(String email) {
        super(ExceptionType.USER_ALREADY_REGISTERED, "User with email " + email + " already exists but is not confirmed.");
    }
}
