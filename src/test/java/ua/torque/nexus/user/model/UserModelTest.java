package ua.torque.nexus.user.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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

    @Test
    void noArgsConstructorShouldBePublicAndInitializeDefaults() throws Exception {
        Constructor<User> ctor = User.class.getDeclaredConstructor();
        assertThat(Modifier.isPublic(ctor.getModifiers())).isTrue();

        User u = ctor.newInstance();

        assertThat(u.getId()).isNull();
        assertThat(u.isEmailConfirmed()).isFalse();
        assertThat(u.getCreated()).isNull();
        assertThat(u.getUpdated()).isNull();
    }

    @Test
    void builderSetsAllFieldsExceptIdAndTimestamps() {
        User u = User.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("Secret123")
                .phoneNumber("+380501234567")
                .build();

        assertThat(u.getFullName()).isEqualTo("John Doe");
        assertThat(u.getEmail()).isEqualTo("john@example.com");
        assertThat(u.getPassword()).isEqualTo("Secret123");
        assertThat(u.getPhoneNumber()).isEqualTo("+380501234567");
        assertThat(u.getRole()).isNull();
        assertThat(u.getId()).isNull();
        assertThat(u.getCreated()).isNull();
        assertThat(u.getUpdated()).isNull();
    }

    @Test
    void setIdShouldBePrivate() throws Exception {
        Method setter = User.class.getDeclaredMethod("setId", Long.class);
        assertThat(Modifier.isPrivate(setter.getModifiers())).isTrue();

        // verify reflection can still set it
        setter.setAccessible(true);
        User u = new User();
        setter.invoke(u, 123L);
        assertThat(u.getId()).isEqualTo(123L);
    }

    @Test
    void toStringDoesNotIncludePassword() {
        User u = User.builder()
                .fullName("Jane Roe")
                .email("jane@domain.com")
                .password("TopSecret")
                .build();

        String repr = u.toString();
        assertThat(repr).doesNotContain("password")
                .contains("fullName=Jane Roe")
                .contains("email=jane@domain.com");
    }

    @Test
    void equalsAndHashCode_withNullAndDifferentClass() {
        User u = User.builder()
                .fullName("Foo Bar")
                .email("foo@bar.com")
                .password("pwd")
                .build();

        assertThat(u)
                .isEqualTo(u)
                .isNotEqualTo(null)
                .isNotEqualTo("some string");

        User other = User.builder()
                .fullName("Foo Bar")
                .email("foo@bar.com")
                .password("pwd2")
                .build();
        assertThat(u).isEqualTo(other).hasSameHashCodeAs(other);

        User diffEmail = User.builder()
                .fullName("Foo Bar")
                .email("different@bar.com")
                .password("pwd")
                .build();
        assertThat(u).isNotEqualTo(diffEmail);
    }
}
