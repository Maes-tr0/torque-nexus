package ua.torque.nexus.user.exception;

public class EmailNotConfirmedException extends RuntimeException {
    public EmailNotConfirmedException(String message) {
        super(message);
    }
}
