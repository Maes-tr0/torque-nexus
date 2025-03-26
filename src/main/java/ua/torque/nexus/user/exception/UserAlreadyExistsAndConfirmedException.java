package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class UserAlreadyExistsAndConfirmedException extends ApplicationException {
    public UserAlreadyExistsAndConfirmedException(String email) {
        super(ExceptionType.USER_ALREADY_EXISTS, "User with email " + email + " already exists and is confirmed.");
    }
}
