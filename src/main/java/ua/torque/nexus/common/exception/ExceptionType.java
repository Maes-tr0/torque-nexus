package ua.torque.nexus.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {
    INPUT_DATA_INVALID(
            "Input data is invalid", HttpStatus.BAD_REQUEST),
    SAME_PASSWORD(
            "New password must be different from the old password", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_ROLE_TYPE(
            "Unsupported role type provided", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(
            "The provided token is invalid or missing", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(
            "Invalid email or password provided", HttpStatus.UNAUTHORIZED),
    EMAIL_NOT_CONFIRMED(
            "User email is not confirmed", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(
            "User with the specified identifier was not found", HttpStatus.NOT_FOUND),
    USER_EMAIL_ALREADY_EXISTS(
            "User with this email already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_CONFIRMED(
            "This email has already been confirmed", HttpStatus.CONFLICT),
    USER_SAVE_FAILED(
            "Failed to save user due to a server error", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_UPDATE_FAILED(
            "Failed to update password due to a server error", HttpStatus.INTERNAL_SERVER_ERROR),
    AUTHENTICATION_FAILED(
            "Authentication failed due to an unexpected error", HttpStatus.INTERNAL_SERVER_ERROR),
    VEHICLE_VIN_ALREADY_EXISTS(
            "Vehicle with this VIN already exists", HttpStatus.CONFLICT),
    VEHICLE_NOT_FOUND(
            "Vehicle with this VIN does not exist", HttpStatus.NOT_FOUND);


    private final String message;
    private final HttpStatus httpStatus;
}