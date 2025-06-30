package ua.torque.nexus.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final ExceptionType type;
    private final HttpStatus status;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Map<String, Object> details;

    protected ApplicationException(ExceptionType type, String message, Map<String, Object> details) {
        super(message);
        this.type = type;
        this.status = type.getHttpStatus();
        this.details = details;
    }

    protected ApplicationException(ExceptionType type, Map<String, Object> details) {
        this(type, type.getMessage(), details);
    }

    protected ApplicationException(ExceptionType type, String message) {
        this(type, message, null);
    }

    protected ApplicationException(ExceptionType type) {
        this(type, type.getMessage(), null);
    }
}
