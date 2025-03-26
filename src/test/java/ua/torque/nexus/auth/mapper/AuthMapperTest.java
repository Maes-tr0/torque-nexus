package ua.torque.nexus.auth.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.torque.nexus.auth.dto.request.RegistrationRequest;
import ua.torque.nexus.auth.dto.response.RegistrationResponse;
import ua.torque.nexus.auth.dto.response.ResetPasswordResponse;
import ua.torque.nexus.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class AuthMapperTest {

    private final AuthMapper mapper = Mappers.getMapper(AuthMapper.class);

    @Test
    void registrationRequestToUser() {
        RegistrationRequest request = RegistrationRequest.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("StrongPassword123")
                .build();

        User user = mapper.registrationRequestToUser(request);

        assertThat(user).isNotNull();
        assertThat(user.getFullName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getPassword()).isEqualTo("StrongPassword123");
    }

    @Test
    void toRegistrationResponse() {
        User user = new User();
        user.setEmail("john.doe@example.com");

        RegistrationResponse response = mapper.toRegistrationResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.message()).isEqualTo("Registration successful — please confirm your email");
    }

    @Test
    void userToResetPasswordResponse() {
        User user = new User();
        user.setEmail("john.doe@example.com");

        ResetPasswordResponse response = mapper.userToResetPasswordResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.message()).isEqualTo("Password successful changed");
    }
}
