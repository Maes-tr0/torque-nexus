package ua.torque.nexus.auth.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.auth.dto.RegistrationRequest;
import ua.torque.nexus.auth.dto.RegistrationResponse;
import ua.torque.nexus.auth.mapper.AuthMapper;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserDataService;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceRegisterTest {

    @Mock
    private AuthMapper authMapper;

    @Mock
    private UserDataService userDataService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_ShouldReturnRegistrationResponse() {
        List<RegistrationRequest> validRequests = List.of(
                RegistrationRequest.builder()
                        .fullName("John Doe")
                        .email("john.doe@example.com")
                        .password("Password1234")
                        .phoneNumber("+1234567890")
                        .build(),
                RegistrationRequest.builder()
                        .fullName("Jane Smith")
                        .email("jane.smith@example.com")
                        .password("Password5678")
                        .phoneNumber("+1987654321")
                        .build()
        );

        for (RegistrationRequest request : validRequests) {
            User mappedUser = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .build();

            ConfirmationToken token = ConfirmationToken.builder()
                    .token("generated-token-" + request.getEmail())
                    .build();

            RegistrationResponse baseResponse = RegistrationResponse.builder()
                    .email(request.getEmail())
                    .message("Registration successful — please confirm your email")
                    .token(null)
                    .build();

            when(authMapper.registrationRequestToUser(request)).thenReturn(mappedUser);
            when(userDataService.saveNewUser(mappedUser)).thenReturn(token);
            when(authMapper.toRegistrationResponse(mappedUser)).thenReturn(baseResponse);

            RegistrationResponse response = authService.registerUser(request);

            verify(authMapper).registrationRequestToUser(request);
            verify(userDataService).saveNewUser(mappedUser);
            verify(authMapper).toRegistrationResponse(mappedUser);

            assertThat(response).isNotNull();
            assertThat(response.email()).isEqualTo(request.getEmail());
            assertThat(response.message()).isEqualTo("Registration successful — please confirm your email");
            assertThat(response.token()).isEqualTo("generated-token-" + request.getEmail());

            clearInvocations(authMapper, userDataService);
        }
    }

    @Test
    void registerUser_ShouldThrowException_WhenSaveFails() {
        List<RegistrationRequest> requests = List.of(
                RegistrationRequest.builder()
                        .fullName("Alice Example")
                        .email("alice@example.com")
                        .password("AlicePass123")
                        .phoneNumber("+1234567891")
                        .build(),
                RegistrationRequest.builder()
                        .fullName("Bob Example")
                        .email("bob@example.com")
                        .password("BobPass123")
                        .phoneNumber("+1234567892")
                        .build()
        );

        for (RegistrationRequest request : requests) {
            User mappedUser = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .build();

            when(authMapper.registrationRequestToUser(request)).thenReturn(mappedUser);
            when(userDataService.saveNewUser(mappedUser))
                    .thenThrow(new RuntimeException("Database error for " + request.getEmail()));

            assertThatThrownBy(() -> authService.registerUser(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Database error for " + request.getEmail());

            verify(authMapper).registrationRequestToUser(request);
            verify(userDataService).saveNewUser(mappedUser);

            clearInvocations(authMapper, userDataService);
        }
    }

    @Test
    void registerUser_ShouldThrowException_WhenMappingFails() {
        List<RegistrationRequest> requests = List.of(
                RegistrationRequest.builder()
                        .fullName("Charlie Example")
                        .email("charlie@example.com")
                        .password("CharliePass123")
                        .phoneNumber("+1234567893")
                        .build(),
                RegistrationRequest.builder()
                        .fullName("Dave Example")
                        .email("dave@example.com")
                        .password("DavePass123")
                        .phoneNumber("+1234567894")
                        .build()
        );

        for (RegistrationRequest request : requests) {
            when(authMapper.registrationRequestToUser(request))
                    .thenThrow(new IllegalArgumentException("Mapping failed for " + request.getEmail()));

            assertThatThrownBy(() -> authService.registerUser(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Mapping failed for " + request.getEmail());

            verify(authMapper).registrationRequestToUser(request);
            clearInvocations(authMapper);
        }
    }

    @Test
    void registerUser_ShouldReturnIncompleteResponse_WhenResponseMappingFails() {
        List<RegistrationRequest> requests = List.of(
                RegistrationRequest.builder()
                        .fullName("Eve Example")
                        .email("eve@example.com")
                        .password("EvePass123")
                        .phoneNumber("+1234567895")
                        .build(),
                RegistrationRequest.builder()
                        .fullName("Frank Example")
                        .email("frank@example.com")
                        .password("FrankPass123")
                        .phoneNumber("+1234567896")
                        .build()
        );

        for (RegistrationRequest request : requests) {
            User mappedUser = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .build();

            ConfirmationToken token = ConfirmationToken.builder()
                    .token("incomplete-token-" + request.getEmail())
                    .build();

            RegistrationResponse incompleteBaseResponse = RegistrationResponse.builder()
                    .email(null)
                    .message(null)
                    .token(null)
                    .build();

            when(authMapper.registrationRequestToUser(request)).thenReturn(mappedUser);
            when(userDataService.saveNewUser(mappedUser)).thenReturn(token);
            when(authMapper.toRegistrationResponse(mappedUser)).thenReturn(incompleteBaseResponse);

            RegistrationResponse response = authService.registerUser(request);

            verify(authMapper).registrationRequestToUser(request);
            verify(userDataService).saveNewUser(mappedUser);
            verify(authMapper).toRegistrationResponse(mappedUser);

            assertThat(response).isNotNull();
            assertThat(response.email()).isNull();
            assertThat(response.message()).isNull();
            assertThat(response.token()).isEqualTo("incomplete-token-" + request.getEmail());

            clearInvocations(authMapper, userDataService);
        }
    }

    @Test
    void registerUser_ShouldThrowValidationException_ForInvalidRequests() {
        List<RegistrationRequest> invalidRequests = List.of(
                RegistrationRequest.builder().build(),
                RegistrationRequest.builder()
                        .fullName("John Doe")
                        .email("invalid-email")
                        .password("Password1234")
                        .phoneNumber("+1234567890")
                        .build(),
                RegistrationRequest.builder()
                        .fullName("john doe")
                        .email("john.doe@example.com")
                        .password("Password1234")
                        .phoneNumber("+1234567890")
                        .build(),
                RegistrationRequest.builder()
                        .fullName("John Doe")
                        .email("john.doe@example.com")
                        .password("password1234")
                        .phoneNumber("+1234567890")
                        .build(),
                RegistrationRequest.builder()
                        .fullName("John Doe")
                        .email("john.doe@example.com")
                        .password("Password1234")
                        .phoneNumber("+123")
                        .build()
        );

        for (RegistrationRequest invalidRequest : invalidRequests) {
            when(authMapper.registrationRequestToUser(invalidRequest))
                    .thenThrow(new ConstraintViolationException("Invalid registration request", new HashSet<>()));

            assertThatThrownBy(() -> authService.registerUser(invalidRequest))
                    .isInstanceOf(ConstraintViolationException.class)
                    .hasMessageContaining("Invalid registration request");

            verify(authMapper).registrationRequestToUser(invalidRequest);

            clearInvocations(authMapper);
        }
    }
}
