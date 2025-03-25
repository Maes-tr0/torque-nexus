package ua.torque.nexus.common.exception.handler;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ua.torque.nexus.common.exception.ApplicationException;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.common.exception.TestApplicationException;
import ua.torque.nexus.common.exception.dto.ErrorResponseDto;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleApplicationException_returnsExpectedResponse() {
        Map<String, Object> details = Map.of("key", "value");
        ApplicationException ex = new TestApplicationException(ExceptionType.INPUT_DATA_INVALID, "Error occurred", details);
        ResponseEntity<Object> response = handler.handleApplicationException(ex);
        ErrorResponseDto body = (ErrorResponseDto) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(ExceptionType.INPUT_DATA_INVALID.getHttpStatus());
        assertThat(body).isNotNull();
        assertThat(body.type()).isEqualTo(ExceptionType.INPUT_DATA_INVALID.name());
        assertThat(body.message()).isEqualTo("Error occurred");
        assertThat(body.details()).isEqualTo(details);
        assertThat(body.timestamp()).isNotNull();
    }

    @Test
    void handleValidation_allFieldsMissing_returnsMissingRequiredField() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dummy");
        bindingResult.addError(new FieldError("dummy", "fullName", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<ErrorResponseDto> response = handler.handleValidation(ex);
        ErrorResponseDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo(ExceptionType.MISSING_REQUIRED_FIELD.name());
        assertThat(dto.message()).isEqualTo(ExceptionType.MISSING_REQUIRED_FIELD.getMessage());
        assertThat(dto.details()).containsKey("fullName");
        assertThat(response.getStatusCode()).isEqualTo(ExceptionType.MISSING_REQUIRED_FIELD.getHttpStatus());
    }

    @Test
    void handleValidation_multipleFieldErrors_returnsMultipleFieldsInvalid() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dummy");
        bindingResult.addError(new FieldError("dummy", "email", "invalid email"));
        bindingResult.addError(new FieldError("dummy", "password", "invalid password"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<ErrorResponseDto> response = handler.handleValidation(ex);
        ErrorResponseDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo(ExceptionType.MULTIPLE_FIELDS_INVALID.name());
        assertThat(response.getStatusCode()).isEqualTo(ExceptionType.MULTIPLE_FIELDS_INVALID.getHttpStatus());
    }

    @Test
    void handleValidation_singleEmailError_returnsEmailInvalid() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dummy");
        bindingResult.addError(new FieldError("dummy", "email", "invalid email format"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<ErrorResponseDto> response = handler.handleValidation(ex);
        ErrorResponseDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo(ExceptionType.EMAIL_INVALID.name());
        assertThat(response.getStatusCode()).isEqualTo(ExceptionType.EMAIL_INVALID.getHttpStatus());
    }

    @Test
    void handleValidation_singlePasswordError_returnsPasswordTooWeak() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dummy");
        bindingResult.addError(new FieldError("dummy", "password", "password too weak"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<ErrorResponseDto> response = handler.handleValidation(ex);
        ErrorResponseDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo(ExceptionType.PASSWORD_TOO_WEAK.name());
        assertThat(response.getStatusCode()).isEqualTo(ExceptionType.PASSWORD_TOO_WEAK.getHttpStatus());
    }

    @Test
    void handleValidation_singleOtherFieldError_returnsInputDataInvalid() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dummy");
        bindingResult.addError(new FieldError("dummy", "username", "invalid username"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<ErrorResponseDto> response = handler.handleValidation(ex);
        ErrorResponseDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo(ExceptionType.INPUT_DATA_INVALID.name());
        assertThat(response.getStatusCode()).isEqualTo(ExceptionType.INPUT_DATA_INVALID.getHttpStatus());
    }

    @Test
    void handleIllegalArg_returnsInputDataInvalidResponse() {
        IllegalArgumentException ex = new IllegalArgumentException("Illegal argument");
        ResponseEntity<ErrorResponseDto> response = handler.handleIllegalArg(ex);
        ErrorResponseDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo(ExceptionType.INPUT_DATA_INVALID.name());
        assertThat(dto.message()).isEqualTo("Illegal argument");
        assertThat(dto.details()).isEmpty();
        assertThat(response.getStatusCode()).isEqualTo(ExceptionType.INPUT_DATA_INVALID.getHttpStatus());
    }

    @Test
    void handleMapping_returnsPayloadMappingError() {
        MappingException ex = new MappingException("Mapping error occurred");
        ResponseEntity<ErrorResponseDto> response = handler.handleMapping(ex);
        ErrorResponseDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo(ExceptionType.INPUT_DATA_INVALID.name());
        assertThat(dto.message()).isEqualTo("Payload mapping error");
        assertThat(dto.details()).containsEntry("error", "Mapping error occurred");
        assertThat(response.getStatusCode()).isEqualTo(ExceptionType.INPUT_DATA_INVALID.getHttpStatus());
    }

    @Test
    void handleDataIntegrity_returnsConflictResponse() {
        RuntimeException cause = new RuntimeException("Constraint violation");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Data integrity error", cause);
        ResponseEntity<ErrorResponseDto> response = handler.handleDataIntegrity(ex);
        ErrorResponseDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo(ExceptionType.USER_SAVE_FAILED.name());
        assertThat(dto.message()).isEqualTo("Database constraint violation");
        assertThat(dto.details()).containsEntry("cause", "Constraint violation");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
