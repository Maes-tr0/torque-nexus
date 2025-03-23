package ua.torque.nexus.user.service;

import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataServiceSaveNewUserTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccessControlService accessControlService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @InjectMocks
    private UserDataService userDataService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .fullName("Test User")
                .email("test@example.com")
                .password("plainPassword")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    void saveNewUser_ShouldSaveUserAndGenerateToken_WhenUserNotRegistered() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.<User>getArgument(0));
        ConfirmationToken token = ConfirmationToken.builder().token("generated-token").build();
        when(confirmationTokenService.generateTokenForUser(any(User.class))).thenReturn(token);

        ConfirmationToken result = userDataService.saveNewUser(user);

        verify(userRepository).findByEmail(user.getEmail());
        verify(accessControlService).assignRoleToUser(user, RoleType.CUSTOMER);
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(user);
        verify(confirmationTokenService).generateTokenForUser(user);
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getToken()).isEqualTo("generated-token");
    }

    @Test
    void saveNewUser_ShouldThrowException_WhenUserAlreadyRegistered() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userDataService.saveNewUser(user))
                .isInstanceOf(UserAlreadyRegisteredException.class)
                .hasMessageContaining("User already registered: " + user.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verifyNoMoreInteractions(accessControlService, passwordEncoder, confirmationTokenService);
    }

    @Test
    void saveNewUser_ShouldThrowUserSaveException_WhenRepositorySaveFails() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        UserSaveException exception = catchThrowableOfType(() -> userDataService.saveNewUser(user),
                UserSaveException.class);
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("Failed to save user: " + user.getEmail());
        assertThat(exception.getDetails()).containsKey("cause");
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).save(user);
    }
}
