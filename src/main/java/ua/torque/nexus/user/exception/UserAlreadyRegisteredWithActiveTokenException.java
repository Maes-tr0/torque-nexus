package ua.torque.nexus.user.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

public class UserAlreadyRegisteredWithActiveTokenException extends ApplicationException {
    public UserAlreadyRegisteredWithActiveTokenException(String message) {
        super(ExceptionType.USER_ALREADY_REGISTERED_WITH_ACTIVE_TOKEN, message);
    }
}
