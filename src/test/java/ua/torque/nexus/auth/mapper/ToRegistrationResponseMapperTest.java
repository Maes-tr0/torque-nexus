package ua.torque.nexus.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.torque.nexus.auth.dto.RegistrationResponse;
import ua.torque.nexus.user.model.User;

class ToRegistrationResponseMapperTest {

    private final AuthMapper mapper = Mappers.getMapper(AuthMapper.class);

    @Test
    void toRegistrationResponse_ShouldMapUserToResponse_WithConstantMessage_ForMultipleCases() {
        List<User> users = List.of(
                User.builder()
                        .fullName("John Doe")
                        .email("john.doe@example.com")
                        .password("hashedPassword1")
                        .phoneNumber("+1234567890")
                        .build(),
                User.builder()
                        .fullName("Alice Smith")
                        .email("alice.smith@example.com")
                        .password("hashedPassword2")
                        .phoneNumber("+1987654321")
                        .build(),
                User.builder()
                        .fullName("Bob Brown")
                        .email("bob.brown@example.com")
                        .password("hashedPassword3")
                        .phoneNumber("+1122334455")
                        .build()
        );

        for (User user : users) {
            RegistrationResponse response = mapper.toRegistrationResponse(user);

            assertThat(response).isNotNull();
            assertThat(response.email()).isEqualTo(user.getEmail());
            assertThat(response.token()).isNull();
            assertThat(response.message()).isEqualTo("Registration successful — please confirm your email");
        }
    }

    @Test
    void toRegistrationResponse_NegativeCases() {
        User nullUser = null;
        RegistrationResponse response = mapper.toRegistrationResponse(nullUser);
        assertThat(response).isNull();

        User userWithNulls = User.builder()
                .fullName("Test User")
                .email(null)
                .password("hashed")
                .phoneNumber(null)
                .build();

        RegistrationResponse response2 = mapper.toRegistrationResponse(userWithNulls);

        assertThat(response2).isNotNull();
        assertThat(response2.email()).isNull();
        assertThat(response2.message()).isEqualTo("Registration successful — please confirm your email");
        assertThat(response2.token()).isNull();

        User userWithEmptyEmail = User.builder()
                .fullName("Empty Email")
                .email("")
                .password("hashed")
                .phoneNumber("+1000000000")
                .build();

        RegistrationResponse response3 = mapper.toRegistrationResponse(userWithEmptyEmail);

        assertThat(response3).isNotNull();
        assertThat(response3.email()).isEqualTo("");
        assertThat(response3.message()).isEqualTo("Registration successful — please confirm your email");
        assertThat(response3.token()).isNull();

        User userWithSpaceEmail = User.builder()
                .fullName("Space Email")
                .email("   ")
                .password("hashed")
                .phoneNumber("+1000000000")
                .build();

        RegistrationResponse response4 = mapper.toRegistrationResponse(userWithSpaceEmail);

        assertThat(response4).isNotNull();
        assertThat(response4.email()).isEqualTo("   ");
        assertThat(response4.message()).isEqualTo("Registration successful — please confirm your email");
        assertThat(response4.token()).isNull();
    }
}
