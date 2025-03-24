package ua.torque.nexus.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.auth.dto.RegistrationRequest;
import ua.torque.nexus.auth.dto.RegistrationResponse;
import ua.torque.nexus.auth.dto.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.ResetPasswordResponse;
import ua.torque.nexus.auth.mapper.AuthMapper;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserDataService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDataService userDataService;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_whenValidRequest_thenReturnRegistrationResponse() {
        RegistrationRequest request = RegistrationRequest.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("Password123")
                .build();

        User user = new User();
        user.setEmail("john.doe@example.com");

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken("dummy-token");

        RegistrationResponse baseResponse = RegistrationResponse.builder()
                .email("john.doe@example.com")
                .message("Registration successful — please confirm your email")
                .token("")
                .build();

        RegistrationResponse expectedResponse = RegistrationResponse.builder()
                .email("john.doe@example.com")
                .message("Registration successful — please confirm your email")
                .token("dummy-token")
                .build();

        when(authMapper.registrationRequestToUser(request)).thenReturn(user);
        when(userDataService.saveNewUser(user)).thenReturn(confirmationToken);
        when(authMapper.toRegistrationResponse(user)).thenReturn(baseResponse);

        RegistrationResponse response = authService.registerUser(request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(userDataService).saveNewUser(user);
    }

    @Test
    void resetPassword_whenValidRequest_thenReturnResetPasswordResponse() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("john.doe@example.com")
                .newPassword("NewPassword456")
                .build();

        User user = new User();
        user.setEmail("john.doe@example.com");

        ResetPasswordResponse expectedResponse = ResetPasswordResponse.builder()
                .email("john.doe@example.com")
                .message("Password successful changed")
                .build();

        when(userDataService.getUserByEmail(request.getEmail())).thenReturn(user);
        when(authMapper.userToResetPasswordResponse(user)).thenReturn(expectedResponse);

        ResetPasswordResponse response = authService.resetPassword(request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(userDataService).updatePasswordUser(user, request.getNewPassword());
    }
}
