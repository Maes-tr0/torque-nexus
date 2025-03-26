package ua.torque.nexus.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDataService – updatePasswordUser() testing")
class UserDataServiceUpdatePasswordUserTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDataService userDataService;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = User.builder()
                .fullName("Test User")
                .email("test@example.com")
                .password("Password1234")
                .build();

        testUser.setEmailConfirmed(true);
    }

    @Test
    void whenUserNotConfirm_thenThrowEmailNotConfirmedException() {
        testUser.setEmailConfirmed(false);

        var ex = assertThrows(
                UserNotFoundException.class,
                () -> userDataService.updatePasswordUser(testUser, "NewPassword123")
        );

        assertTrue(ex.getMessage().contains(testUser.getEmail()));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenNewPasswordSameAsExistingPassword_thenThrowSamePasswordException() {
        String sameRawPassword = "Password1234";

        when(passwordEncoder.matches(sameRawPassword, testUser.getPassword())).thenReturn(true);

        var ex = assertThrows(
                SamePasswordException.class,
                () -> userDataService.updatePasswordUser(testUser, sameRawPassword)
        );

        assertEquals("New password must be different from the old password", ex.getMessage());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenConfirmedUserAndNewPasswordNotTheSameAsExistingPassword_thenUpdatePasswordSuccessfully() {
        String newRawPassword = "NewPassword1234";

        when(passwordEncoder.matches(newRawPassword, testUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(newRawPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);

        assertDoesNotThrow(() -> userDataService.updatePasswordUser(testUser, newRawPassword));
        assertEquals("encodedNewPassword", testUser.getPassword());
        verify(passwordEncoder).encode(newRawPassword);
        verify(userRepository).save(testUser);
    }

    @Test
    void whenUserRepositoryCantSaveNewPassword_thenThrowPasswordUpdateException() {
        testUser.setEmailConfirmed(true);
        String newRawPassword = "";

        when(passwordEncoder.matches(newRawPassword, testUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(newRawPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(testUser)).thenThrow(new RuntimeException("DB error"));

        var ex = assertThrows(
                PasswordUpdateException.class,
                () -> userDataService.updatePasswordUser(testUser, newRawPassword)
        );

        assertTrue(ex.getMessage().contains("Failed to update password for user"));
        verify(passwordEncoder).encode(newRawPassword);
        verify(userRepository).save(testUser);
    }
}
