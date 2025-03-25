package ua.torque.nexus.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.common.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException e) {
        log.error("ApplicationException — type: {}, message: {}", e.getType(), e.getMessage(), e);
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .type(e.getType().name())
                .message(e.getMessage())
                .details(e.getDetails())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(e.getType().getHttpStatus()).body(errorDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException e) {
        Map<String, Object> details = new HashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            details.put(fe.getField(), Objects.requireNonNullElse(fe.getDefaultMessage(), "Invalid value"));
        }

        ExceptionType type = getExceptionType(e, details);

        ErrorResponseDto dto = ErrorResponseDto.builder()
                .type(type.name())
                .message(type.getMessage())
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(type.getHttpStatus()).body(dto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArg(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage(), e);
        return buildResponse(e.getMessage(), Map.of());
    }

    @ExceptionHandler(MappingException.class)
    public ResponseEntity<ErrorResponseDto> handleMapping(MappingException e) {
        log.error("MappingException: {}", e.getMessage(), e);
        return buildResponse("Payload mapping error", Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrity(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException: {}", e.getMostSpecificCause().getMessage(), e);
        ExceptionType type = ExceptionType.USER_SAVE_FAILED;
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .type(type.name())
                .message("Database constraint violation")
                .details(Map.of("cause", e.getMostSpecificCause().getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(dto);
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(String msg, Map<String, Object> details) {
        ExceptionType type = ExceptionType.INPUT_DATA_INVALID;
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .type(type.name())
                .message(msg)
                .details(details == null ? Map.of() : details)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(type.getHttpStatus()).body(dto);
    }

    private static ExceptionType getExceptionType(MethodArgumentNotValidException e, Map<String, Object> details) {
        boolean allMissing = details.values().stream()
                .map(Object::toString)
                .allMatch(msg -> msg.toLowerCase().contains("blank") || msg.toLowerCase().contains("null"));

        if (allMissing) {
            return ExceptionType.MISSING_REQUIRED_FIELD;
        }

        if (details.size() > 1) {
            return ExceptionType.MULTIPLE_FIELDS_INVALID;
        }

        FieldError fe = e.getBindingResult().getFieldErrors().getFirst();
        return switch (fe.getField()) {
            case "email"    -> ExceptionType.EMAIL_INVALID;
            case "password" -> ExceptionType.PASSWORD_TOO_WEAK;
            default         -> ExceptionType.INPUT_DATA_INVALID;
        };
    }
}
