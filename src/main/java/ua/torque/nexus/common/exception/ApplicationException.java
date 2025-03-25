package ua.torque.nexus.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final ExceptionType type;
    private final HttpStatus status;

    /**
     * Поле details тимчасово позначене як transient.
     * У майбутньому, коли знадобиться серіалізація ApplicationException
     * (наприклад, для збереження помилок у логах, відправки їх у віддалене сховище або інші механізми збереження),
     * цей модифікатор можна буде прибрати, і поле стане частиною Java‑серіалізації.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final transient Map<String, Object> details;

    protected ApplicationException(ExceptionType type, String message, Map<String, Object> details) {
        super(message);
        this.type = type;
        this.status = type.getHttpStatus();
        this.details = details;
    }

    protected ApplicationException(ExceptionType type, String message) {
        this(type, message, null);
    }
}
