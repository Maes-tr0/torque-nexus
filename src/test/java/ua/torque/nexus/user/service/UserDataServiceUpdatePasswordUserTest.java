package ua.torque.nexus.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.torque.nexus.access.exception.PasswordUpdateException;
import ua.torque.nexus.user.exception.EmailNotConfirmedException;
import ua.torque.nexus.user.exception.SamePasswordException;
import ua.torque.nexus.user.exception.UserNotFoundException;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataServiceUpdatePasswordUserTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDataService userDataService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .fullName("Test User")
                .email("test@example.com")
                .password("encodedOldPassword")
                .phoneNumber("+1234567890")
                .build();
        user.setEmailConfirmed(true);
    }

    @Test
    void updatePasswordUser_ShouldUpdatePassword_WhenValidNewPassword() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("NewPassword123", user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);

        userDataService.updatePasswordUser(user, "NewPassword123");

        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoder).matches("NewPassword123", "encodedOldPassword");
        verify(passwordEncoder).encode("NewPassword123");
        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }


    @Test
    void updatePasswordUser_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDataService.updatePasswordUser(user, "NewPassword123"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found: " + user.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void updatePasswordUser_ShouldThrowEmailNotConfirmedException_WhenUserNotConfirmed() {
        user.setEmailConfirmed(false);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userDataService.updatePasswordUser(user, "NewPassword123"))
                .isInstanceOf(EmailNotConfirmedException.class)
                .hasMessageContaining("User is not confirmed: " + user.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void updatePasswordUser_ShouldThrowSamePasswordException_WhenNewPasswordIsSame() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("SamePassword", user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> userDataService.updatePasswordUser(user, "SamePassword"))
                .isInstanceOf(SamePasswordException.class)
                .hasMessageContaining("New password must be different from the old password");

        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoder).matches("SamePassword", user.getPassword());
    }

    @Test
    void updatePasswordUser_ShouldThrowPasswordUpdateException_WhenSaveFails() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("NewPassword123", user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenThrow(new RuntimeException("DB error"));

        PasswordUpdateException ex = catchThrowableOfType(
                () -> userDataService.updatePasswordUser(user, "NewPassword123"),
                PasswordUpdateException.class
        );
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).contains("Failed to update password for user: " + user.getEmail());
        assertThat(ex.getDetails()).containsKeys("cause", "message");
    }
}
