package ua.torque.nexus.user.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Model — Validation & Behavior")
class UserModelTest {

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private User createValidUser() {
        Role role = new Role();
        role.setType(RoleType.CUSTOMER);

        User u = User.builder()
                .fullName("Alice Smith")
                .email("alice@example.com")
                .password("Password123")
                .phoneNumber("+12345678901")
                .build();
        u.setRole(role);
        u.setEmailConfirmed(true);
        return u;
    }

    @Test
    void validUser_noViolations() {
        assertThat(validator.validate(createValidUser())).isEmpty();
    }

    @Test
    void nullFullName() {
        User u = createValidUser();
        u.setFullName(null);
        assertThat(validator.validate(u))
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactly("fullName");
    }

    @Test
    void badFullNameFormat() {
        User u = createValidUser();
        u.setFullName("alice");
        assertThat(validator.validate(u))
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactly("fullName");
    }

    @Test
    void nullEmail() {
        User u = createValidUser();
        u.setEmail(null);
        assertThat(validator.validate(u))
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactly("email");
    }

    @Test
    void badEmail() {
        User u = createValidUser();
        u.setEmail("alice");
        assertThat(validator.validate(u))
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactly("email");
    }

    @Test
    void nullPassword() {
        User u = createValidUser();
        u.setPassword(null);
        assertThat(validator.validate(u))
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactly("password");
    }

    @Test
    void missingPhoneAllowed() {
        User u = createValidUser();
        u.setPhoneNumber(null);
        assertThat(validator.validate(u)).isEmpty();
    }

    @Test
    void shortPhone() {
        User u = createValidUser();
        u.setPhoneNumber("123");
        assertThat(validator.validate(u))
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactly("phoneNumber");
    }

    @Test
    void badPhonePattern() {
        User u = createValidUser();
        u.setPhoneNumber("abc123");
        assertThat(validator.validate(u))
                .extracting(v -> v.getPropertyPath().toString())
                .containsOnly("phoneNumber");

    }

    @Test
    void equalsHash_sameEmail() {
        User u1 = createValidUser();
        User u2 = createValidUser();
        u2.setFullName("Different Name");
        assertThat(u1).isEqualTo(u2).hasSameHashCodeAs(u2);
    }

    @Test
    void equalsDifferentEmail() {
        User u1 = createValidUser();
        User u2 = createValidUser();
        u2.setEmail("other@example.com");
        assertThat(u1).isNotEqualTo(u2);
    }
}
