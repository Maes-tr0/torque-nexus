package ua.torque.nexus.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.access.exception.PasswordUpdateException;
import ua.torque.nexus.auth.dto.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.ResetPasswordResponse;
import ua.torque.nexus.auth.mapper.AuthMapper;
import ua.torque.nexus.user.exception.EmailNotConfirmedException;
import ua.torque.nexus.user.exception.SamePasswordException;
import ua.torque.nexus.user.exception.UserNotFoundException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserDataService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceResetPasswordTest {

    @Mock
    private UserDataService userDataService;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    void resetPassword_ShouldReturnResetPasswordResponse_ForMultipleValidRequests() {
        List<ResetPasswordRequest> validRequests = List.of(
                ResetPasswordRequest.builder()
                        .email("john.doe@example.com")
                        .newPassword("NewPassword123")
                        .build(),
                ResetPasswordRequest.builder()
                        .email("jane.doe@example.com")
                        .newPassword("AnotherPass123")
                        .build()
        );

        for (ResetPasswordRequest request : validRequests) {
            User user = User.builder()
                    .fullName("Test User")
                    .email(request.getEmail())
                    .password("oldHashedPassword")
                    .build();

            ResetPasswordResponse baseResponse = ResetPasswordResponse.builder()
                    .email(request.getEmail())
                    .message("Password successful changed")
                    .build();

            when(userDataService.getUserByEmail(request.getEmail())).thenReturn(user);
            when(authMapper.userToResetPasswordResponse(user)).thenReturn(baseResponse);

            ResetPasswordResponse response = authService.resetPassword(request);

            verify(userDataService).getUserByEmail(request.getEmail());
            verify(userDataService).updatePasswordUser(user, request.getNewPassword());
            verify(authMapper).userToResetPasswordResponse(user);

            assertThat(response).isNotNull();
            assertThat(response.email()).isEqualTo(request.getEmail());
            assertThat(response.message()).isEqualTo("Password successful changed");

            clearInvocations(userDataService, authMapper);
        }
    }

    @Test
    void resetPassword_ShouldThrowUserNotFoundException_ForMultipleEmails() {
        List<ResetPasswordRequest> requests = List.of(
                ResetPasswordRequest.builder().email("nonexistent1@example.com").newPassword("Pass12345").build(),
                ResetPasswordRequest.builder().email("nonexistent2@example.com").newPassword("Pass12345").build()
        );

        for (ResetPasswordRequest request : requests) {
            when(userDataService.getUserByEmail(request.getEmail()))
                    .thenThrow(new UserNotFoundException("User not found: " + request.getEmail()));

            assertThatThrownBy(() -> authService.resetPassword(request))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("User not found: " + request.getEmail());

            verify(userDataService).getUserByEmail(request.getEmail());
            clearInvocations(userDataService, authMapper);
        }
    }

    @Test
    void resetPassword_ShouldThrowEmailNotConfirmedException_ForMultipleCases() {
        List<ResetPasswordRequest> requests = List.of(
                ResetPasswordRequest.builder().email("user1@example.com").newPassword("NewPass123").build(),
                ResetPasswordRequest.builder().email("user2@example.com").newPassword("NewPass123").build()
        );

        for (ResetPasswordRequest request : requests) {
            User user = User.builder()
                    .fullName("Test User")
                    .email(request.getEmail())
                    .password("oldHashedPassword")
                    .build();

            when(userDataService.getUserByEmail(request.getEmail())).thenReturn(user);
            doThrow(new EmailNotConfirmedException("User is not confirmed: " + request.getEmail()))
                    .when(userDataService).updatePasswordUser(user, request.getNewPassword());

            assertThatThrownBy(() -> authService.resetPassword(request))
                    .isInstanceOf(EmailNotConfirmedException.class)
                    .hasMessageContaining("User is not confirmed: " + request.getEmail());

            verify(userDataService).getUserByEmail(request.getEmail());
            verify(userDataService).updatePasswordUser(user, request.getNewPassword());
            clearInvocations(userDataService, authMapper);
        }
    }

    @Test
    void resetPassword_ShouldThrowSamePasswordException_ForMultipleCases() {
        List<ResetPasswordRequest> requests = List.of(
                ResetPasswordRequest.builder().email("user1@example.com").newPassword("SamePassword123").build(),
                ResetPasswordRequest.builder().email("user2@example.com").newPassword("SamePassword123").build()
        );

        for (ResetPasswordRequest request : requests) {
            User user = User.builder()
                    .fullName("Test User")
                    .email(request.getEmail())
                    .password("hashedSamePassword")
                    .build();

            when(userDataService.getUserByEmail(request.getEmail())).thenReturn(user);
            doThrow(new SamePasswordException("New password must be different from the old password"))
                    .when(userDataService).updatePasswordUser(user, request.getNewPassword());

            assertThatThrownBy(() -> authService.resetPassword(request))
                    .isInstanceOf(SamePasswordException.class)
                    .hasMessageContaining("New password must be different from the old password");

            verify(userDataService).getUserByEmail(request.getEmail());
            verify(userDataService).updatePasswordUser(user, request.getNewPassword());
            clearInvocations(userDataService, authMapper);
        }
    }

    @Test
    void resetPassword_ShouldThrowPasswordUpdateException_ForMultipleCases() {
        List<ResetPasswordRequest> requests = List.of(
                ResetPasswordRequest.builder().email("user1@example.com").newPassword("NewPass123").build(),
                ResetPasswordRequest.builder().email("user2@example.com").newPassword("NewPass123").build()
        );

        for (ResetPasswordRequest request : requests) {
            User user = User.builder()
                    .fullName("Test User")
                    .email(request.getEmail())
                    .password("oldHashedPassword")
                    .build();

            when(userDataService.getUserByEmail(request.getEmail())).thenReturn(user);
            doThrow(new PasswordUpdateException("Failed to update password for user: " + request.getEmail()))
                    .when(userDataService).updatePasswordUser(user, request.getNewPassword());

            assertThatThrownBy(() -> authService.resetPassword(request))
                    .isInstanceOf(PasswordUpdateException.class)
                    .hasMessageContaining("Failed to update password for user: " + request.getEmail());

            verify(userDataService).getUserByEmail(request.getEmail());
            verify(userDataService).updatePasswordUser(user, request.getNewPassword());
            clearInvocations(userDataService, authMapper);
        }
    }
}
