package ua.torque.nexus.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.torque.nexus.auth.dto.request.RegistrationRequest;
import ua.torque.nexus.auth.dto.request.ResetPasswordRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRegistrationRequest_shouldPassValidation() {
        RegistrationRequest request = RegistrationRequest.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("Password1")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void invalidRegistrationRequest_whenFullNameNotMatchPattern_shouldFail() {
        RegistrationRequest request = RegistrationRequest.builder()
                .fullName("john doe")
                .email("john.doe@example.com")
                .password("Password1")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fullName"));
    }

    @Test
    void invalidRegistrationRequest_whenInvalidEmail_shouldFail() {
        RegistrationRequest request = RegistrationRequest.builder()
                .fullName("John Doe")
                .email("invalid-email")
                .password("Password1")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void invalidRegistrationRequest_whenWeakPassword_shouldFail() {
        RegistrationRequest request = RegistrationRequest.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("password")
                .phoneNumber("+1234567890")
                .build();

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void validResetPasswordRequest_shouldPassValidation() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("john.doe@example.com")
                .newPassword("NewPassword1")
                .build();

        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void invalidResetPasswordRequest_whenNewPasswordWeak_shouldFail() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("john.doe@example.com")
                .newPassword("weakpass")
                .build();

        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("newPassword"));
    }
}
