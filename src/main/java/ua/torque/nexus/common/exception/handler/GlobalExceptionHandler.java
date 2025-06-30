package ua.torque.nexus.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.common.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponseDto> handleApplicationException(ApplicationException e) {
        log.error("ApplicationException -> type: {}, message: {}", e.getType(), e.getMessage(), e);
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .type(e.getType().name())
                .message(e.getMessage())
                .details(e.getDetails())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(e.getType().getHttpStatus()).body(errorDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException e) {
        ExceptionType type = ExceptionType.INPUT_DATA_INVALID;
        log.warn("Validation failed for request: {}", e.getMessage());

        Map<String, Object> validationErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .type(type.name())
                .message(type.getMessage())
                .details(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(type.getHttpStatus()).body(errorDto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Data integrity violation: {}", e.getMostSpecificCause().getMessage(), e);

        ExceptionType type = ExceptionType.USER_EMAIL_ALREADY_EXISTS;

        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .type(type.name())
                .message("A resource with the same unique identifier already exists.")
                .details(Map.of("error", "Database constraint violation"))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(type.getHttpStatus()).body(errorDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllUncaughtException(Exception e) {
        log.error("An unexpected server error occurred", e);

        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .type("UNEXPECTED_SERVER_ERROR")
                .message("An unexpected server error has occurred. Please contact support.")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }
}