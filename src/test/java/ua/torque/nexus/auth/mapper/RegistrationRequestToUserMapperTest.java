package ua.torque.nexus.auth.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.torque.nexus.auth.dto.RegistrationRequest;
import ua.torque.nexus.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationRequestToUserMapperTest {

    private final AuthMapper mapper = Mappers.getMapper(AuthMapper.class);

    @Test
    void registrationRequestToUser_ShouldMapFieldsCorrectly_ForMultipleCases() {
        List<RegistrationRequest> requests = List.of(
                RegistrationRequest.builder()
                        .fullName("John Doe")
                        .email("john.doe@example.com")
                        .password("Password1234")
                        .phoneNumber("+1234567890")
                        .build(),
                RegistrationRequest.builder()
                        .fullName("Alice Smith")
                        .email("alice.smith@example.com")
                        .password("AlicePass123")
                        .phoneNumber("+1987654321")
                        .build(),
                RegistrationRequest.builder()
                        .fullName("Bob Brown")
                        .email("bob.brown@example.com")
                        .password("BobPass1234")
                        .phoneNumber("+1122334455")
                        .build()
        );

        for (RegistrationRequest req : requests) {
            User user = mapper.registrationRequestToUser(req);

            assertThat(user).isNotNull();
            assertThat(user.getFullName()).isEqualTo(req.getFullName());
            assertThat(user.getEmail()).isEqualTo(req.getEmail());
            assertThat(user.getPassword()).isEqualTo(req.getPassword());
            assertThat(user.getPhoneNumber()).isEqualTo(req.getPhoneNumber());
        }
    }

    @Test
    void registrationRequestToUser_NegativeCases() {
        RegistrationRequest nullRequest = null;
        User user = mapper.registrationRequestToUser(nullRequest);
        assertThat(user).isNull();

        RegistrationRequest invalidRequest = RegistrationRequest.builder()
                .fullName("invalid")
                .email("not-an-email")
                .password("weak")
                .phoneNumber("123")
                .build();

        User mapped = mapper.registrationRequestToUser(invalidRequest);

        assertThat(mapped).isNotNull();
        assertThat(mapped.getFullName()).isEqualTo("invalid");
        assertThat(mapped.getEmail()).isEqualTo("not-an-email");
        assertThat(mapped.getPassword()).isEqualTo("weak");
        assertThat(mapped.getPhoneNumber()).isEqualTo("123");
    }

    @Test
    void registrationRequestToUser_ShouldMapPartialDataCorrectly() {
        RegistrationRequest partialRequest = RegistrationRequest.builder()
                .fullName("Partial User")
                .email(null)
                .password("PartialPass123")
                .phoneNumber(null)
                .build();

        User mapped = mapper.registrationRequestToUser(partialRequest);

        assertThat(mapped).isNotNull();
        assertThat(mapped.getFullName()).isEqualTo("Partial User");
        assertThat(mapped.getEmail()).isNull();
        assertThat(mapped.getPassword()).isEqualTo("PartialPass123");
        assertThat(mapped.getPhoneNumber()).isNull();
    }

    @Test
    void registrationRequestToUser_ShouldMapEmptyStringsCorrectly() {
        RegistrationRequest emptyStringsRequest = RegistrationRequest.builder()
                .fullName("")
                .email("")
                .password("")
                .phoneNumber("")
                .build();

        User mapped = mapper.registrationRequestToUser(emptyStringsRequest);

        assertThat(mapped).isNotNull();
        assertThat(mapped.getFullName()).isEmpty();
        assertThat(mapped.getEmail()).isEmpty();
        assertThat(mapped.getPassword()).isEmpty();
        assertThat(mapped.getPhoneNumber()).isEmpty();
    }
}
