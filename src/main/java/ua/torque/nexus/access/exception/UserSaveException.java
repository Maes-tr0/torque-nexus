package ua.torque.nexus.access.exception;

import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;

import java.util.Map;

public class UserSaveException extends ApplicationException {

    public UserSaveException(String message) {
        super(ExceptionType.USER_SAVE_FAILED, message);
    }

    public UserSaveException(String message, Map<String, Object> details) {
        super(ExceptionType.USER_SAVE_FAILED, message, details);
    }
}
