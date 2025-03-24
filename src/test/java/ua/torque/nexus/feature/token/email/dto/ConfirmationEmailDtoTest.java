package ua.torque.nexus.feature.token.email.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmationEmailDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void confirmationEmailResponse_builderSetsFields() {
        LocalDateTime time = LocalDateTime.now();
        ConfirmationEmailResponse response = ConfirmationEmailResponse.builder()
                .token("testToken")
                .confirmedAt(time)
                .message("Confirmed successfully")
                .build();
        assertThat(response.token()).isEqualTo("testToken");
        assertThat(response.confirmedAt()).isEqualTo(time);
        assertThat(response.message()).isEqualTo("Confirmed successfully");
    }

    @Test
    void confirmationEmailRequest_validToken_noViolations() {
        ConfirmationEmailRequest request = ConfirmationEmailRequest.builder()
                .token("nonBlankToken")
                .build();
        Set<ConstraintViolation<ConfirmationEmailRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void confirmationEmailRequest_blankToken_violations() {
        ConfirmationEmailRequest request = ConfirmationEmailRequest.builder()
                .token("")
                .build();
        Set<ConstraintViolation<ConfirmationEmailRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
    }

    @Test
    void confirmationEmailRequest_equalsAndHashCode() {
        ConfirmationEmailRequest r1 = ConfirmationEmailRequest.builder().token("abc").build();
        ConfirmationEmailRequest r2 = ConfirmationEmailRequest.builder().token("abc").build();
        ConfirmationEmailRequest r3 = ConfirmationEmailRequest.builder().token("xyz").build();
        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        assertThat(r1).isNotEqualTo(r3);
    }
}
