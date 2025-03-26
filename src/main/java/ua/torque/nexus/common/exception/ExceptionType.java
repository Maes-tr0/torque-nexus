package ua.torque.nexus.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {
    USER_NOT_FOUND(
            "User with this id not found or not exists", HttpStatus.NOT_FOUND),
    INPUT_DATA_INVALID(
            "Input data is invalid", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(
            "Invalid email format", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK(
            "Password is too weak", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD(
            "Required field is missing", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_CONFIRMED(
            "Email is already confirmed", HttpStatus.CONFLICT),
    TOKEN_EXPIRED(
            "Token is expired", HttpStatus.GONE),
    TOKEN_NOT_FOUND(
            "Token was not found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_CONFIRMED(
            "Email is not confirmed", HttpStatus.FORBIDDEN),
    SAME_PASSWORD(
            "New password must be different from the old password", HttpStatus.BAD_REQUEST),
    USER_ALREADY_REGISTERED(
            "User is already registered", HttpStatus.CONFLICT),
    USER_ALREADY_EXISTS(
            "User already registered and confirmation token is still active", HttpStatus.CONFLICT),
    PASSWORD_UPDATE_FAILED(
            "Failed to update password", HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_ROLE_TYPE(
            "Unsupported role type", HttpStatus.BAD_REQUEST),
    MULTIPLE_FIELDS_INVALID(
            "Multiple fields are invalid", HttpStatus.BAD_REQUEST),
    USER_SAVE_FAILED(
            "Failed to save user", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_CREDENTIALS(
            "Invalid credentials provided", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus httpStatus;
}
