package ua.torque.nexus.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.torque.nexus.auth.dto.ResetPasswordResponse;
import ua.torque.nexus.user.model.User;

class UserToResetPasswordResponseMapperTest {

    private final AuthMapper mapper = Mappers.getMapper(AuthMapper.class);

    @Test
    void userToResetPasswordResponse_ShouldMapUserToResetResponse_WithConstantMessage_ForMultipleCases() {
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
            ResetPasswordResponse response = mapper.userToResetPasswordResponse(user);

            assertThat(response).isNotNull();
            // Email is mapped
            assertThat(response.email()).isEqualTo(user.getEmail());
            // Message is set to constant as defined in mapping
            assertThat(response.message()).isEqualTo("Password successful changed");
        }
    }

    @Test
    void userToResetPasswordResponse_NegativeCases() {
        // Case 1: Null user input should return null
        User nullUser = null;
        ResetPasswordResponse response = mapper.userToResetPasswordResponse(nullUser);
        assertThat(response).isNull();

        // Case 2: User with null email – mapping should copy null email, message is constant
        User userWithNullEmail = User.builder()
                .fullName("Test User")
                .email(null)
                .password("hashed")
                .phoneNumber("1234567890")
                .build();
        ResetPasswordResponse response2 = mapper.userToResetPasswordResponse(userWithNullEmail);
        assertThat(response2).isNotNull();
        assertThat(response2.email()).isNull();
        assertThat(response2.message()).isEqualTo("Password successful changed");
    }

    @Test
    void userToResetPasswordResponse_AdditionalNegativeCases() {
        User userWithEmptyEmail = User.builder()
                .fullName("Empty Email")
                .email("")
                .password("hashedPassword")
                .phoneNumber("+1234567890")
                .build();

        ResetPasswordResponse response3 = mapper.userToResetPasswordResponse(userWithEmptyEmail);

        assertThat(response3).isNotNull();
        assertThat(response3.email()).isEmpty();
        assertThat(response3.message()).isEqualTo("Password successful changed");

        User userWithSpaceEmail = User.builder()
                .fullName("Space Email")
                .email("   ")
                .password("hashedPassword")
                .phoneNumber("+1234567890")
                .build();

        ResetPasswordResponse response4 = mapper.userToResetPasswordResponse(userWithSpaceEmail);

        assertThat(response4).isNotNull();
        assertThat(response4.email()).isEqualTo("   ");
        assertThat(response4.message()).isEqualTo("Password successful changed");

        User userWithNullFullName = User.builder()
                .fullName(null)
                .email("null.full@example.com")
                .password("hashedPassword")
                .phoneNumber("+1234567890")
                .build();

        ResetPasswordResponse response5 = mapper.userToResetPasswordResponse(userWithNullFullName);

        assertThat(response5).isNotNull();
        assertThat(response5.email()).isEqualTo("null.full@example.com");
        assertThat(response5.message()).isEqualTo("Password successful changed");
    }
}
