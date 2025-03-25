package ua.torque.nexus.user.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.torque.nexus.access.exception.UserSaveException;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.service.AccessControlService;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.service.ConfirmationTokenService;
import ua.torque.nexus.user.exception.UserAlreadyRegisteredWithActiveTokenException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDataService – saveNewUser() testing")
class UserDataServiceSaveNewUserTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private AccessControlService accessControlService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserDataService userDataService;

    private static User testUser;

    @BeforeAll
    static void setup() {
        testUser = User.builder()
                .fullName("Test User")
                .email("test@example.com")
                .password("Password1234")
                .build();
    }

    @Test
    void whenEmailAlreadyExistsAndTokenNotExpired_thenThrowException() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(confirmationTokenService.isTokenExpired(testUser)).thenReturn(false);

        assertThrows(UserAlreadyRegisteredWithActiveTokenException.class,
                () -> userDataService.saveNewUser(testUser));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(confirmationTokenService, never()).generateTokenForUser(any());
    }

    @Test
    void whenEmailAlreadyExistsAndTokenExpired_thenGenerateNewToken() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(confirmationTokenService.isTokenExpired(testUser)).thenReturn(true);

        ConfirmationToken token = new ConfirmationToken();
        when(confirmationTokenService.generateTokenForUser(testUser)).thenReturn(token);

        ConfirmationToken result = userDataService.saveNewUser(testUser);

        assertSame(token, result);
        verify(accessControlService, never()).assignRoleToUser(any(), any());
        verify(userRepository, never()).save(any());
        verify(confirmationTokenService).generateTokenForUser(testUser);
    }

    @Test
    void whenValidNewUser_thenAssignRoleEncodeSaveAndGenerateToken() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);

        ConfirmationToken token = new ConfirmationToken();
        when(confirmationTokenService.generateTokenForUser(testUser)).thenReturn(token);

        ConfirmationToken result = userDataService.saveNewUser(testUser);

        assertSame(token, result);
        assertEquals("encodedPassword", testUser.getPassword());
        verify(accessControlService).assignRoleToUser(testUser, RoleType.CUSTOMER);
        verify(userRepository).save(testUser);
        verify(confirmationTokenService).generateTokenForUser(testUser);
    }

    @Test
    void whenRepositorySaveFails_thenThrowUserSaveException() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(testUser)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> userDataService.saveNewUser(testUser))
                .isInstanceOf(UserSaveException.class)
                .hasMessageContaining(testUser.getEmail());

        verify(confirmationTokenService, never()).generateTokenForUser(any());
    }
}
