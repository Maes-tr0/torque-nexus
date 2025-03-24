package ua.torque.nexus.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import ua.torque.nexus.user.exception.UserAlreadyRegisteredException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.util.Optional;

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
    void whenEmailAlreadyExists_thenThrowUserAlreadyRegisteredException() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        UserAlreadyRegisteredException ex = assertThrows(
                UserAlreadyRegisteredException.class,
                () -> userDataService.saveNewUser(testUser)
        );

        assertTrue(ex.getMessage().contains(testUser.getEmail()));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(confirmationTokenService, never()).generateTokenForUser(any());
    }

    @Test
    void whenValidNewUser_thenReturnConfirmationToken() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);

        ConfirmationToken token = new ConfirmationToken();
        when(confirmationTokenService.generateTokenForUser(testUser)).thenReturn(token);

        ConfirmationToken actual = userDataService.saveNewUser(testUser);

        assertSame(token, actual);
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

        UserSaveException ex = assertThrows(
                UserSaveException.class,
                () -> userDataService.saveNewUser(testUser)
        );

        assertTrue(ex.getMessage().contains(testUser.getEmail()));
        verify(confirmationTokenService, never()).generateTokenForUser(any());
    }
}
