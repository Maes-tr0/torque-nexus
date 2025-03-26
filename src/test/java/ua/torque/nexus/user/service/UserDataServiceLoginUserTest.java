package ua.torque.nexus.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.security.JwtTokenService;
import ua.torque.nexus.user.exception.InvalidCredentialsException;
import ua.torque.nexus.user.exception.UserNotFoundException;
import ua.torque.nexus.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataServiceLoginUserTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Spy
    @InjectMocks
    private UserDataService userDataService;

    @Test
    void testLoginUserSuccess() {
        String email = "john.doe@example.com";
        String rawPassword = "password";

        User user = User.builder()
                .fullName("John Doe")
                .email(email)
                .password("hashedPassword")
                .phoneNumber("+1234567890")
                .build();
        user.setEmailConfirmed(true);

        doReturn(user).when(userDataService).getUserByEmail(email);
        doReturn(true).when(userDataService).isSamePassword(user.getPassword(), rawPassword);
        when(jwtTokenService.generateTokenForUser(user)).thenReturn("jwtToken");

        String token = userDataService.loginUser(email, rawPassword);

        assertEquals("jwtToken", token);
    }

    @Test
    void testLoginUserEmailNotConfirmed() {
        String email = "john.doe@example.com";
        String rawPassword = "password";

        User user = User.builder()
                .fullName("John Doe")
                .email(email)
                .password("hashedPassword")
                .phoneNumber("+1234567890")
                .build();

        doReturn(user).when(userDataService).getUserByEmail(email);

        assertThrows(UserNotFoundException.class, () -> userDataService.loginUser(email, rawPassword));
    }

    @Test
    void testLoginUserInvalidPassword() {
        String email = "john.doe@example.com";
        String rawPassword = "wrongPassword";

        User user = User.builder()
                .fullName("John Doe")
                .email(email)
                .password("hashedPassword")
                .phoneNumber("+1234567890")
                .build();
        user.setEmailConfirmed(true);

        doReturn(user).when(userDataService).getUserByEmail(email);
        doReturn(false).when(userDataService).isSamePassword(user.getPassword(), rawPassword);

        assertThrows(InvalidCredentialsException.class, () -> userDataService.loginUser(email, rawPassword));
    }

    @Test
    void testLoginUserUserNotFound() {
        String email = "nonexistent@example.com";
        String rawPassword = "password";

        doReturn(null).when(userDataService).getUserByEmail(email);

        assertThrows(NullPointerException.class, () -> userDataService.loginUser(email, rawPassword));
    }

    @Test
    void testLoginUserNullPassword() {
        String email = "john.doe@example.com";

        User user = User.builder()
                .fullName("John Doe")
                .email(email)
                .password("hashedPassword")
                .phoneNumber("+1234567890")
                .build();
        user.setEmailConfirmed(true);

        doReturn(user).when(userDataService).getUserByEmail(email);
        doReturn(false).when(userDataService).isSamePassword(user.getPassword(), null);

        assertThrows(InvalidCredentialsException.class, () -> userDataService.loginUser(email, null));
    }

    @Test
    void testLoginUserNullEmail() {
        String rawPassword = "password";

        doReturn(null).when(userDataService).getUserByEmail(null);

        assertThrows(NullPointerException.class, () -> userDataService.loginUser(null, rawPassword));
    }

    @Test
    void testLoginUserTokenGenerationReturnsNull() {
        String email = "john.doe@example.com";
        String rawPassword = "password";

        User user = User.builder()
                .fullName("John Doe")
                .email(email)
                .password("hashedPassword")
                .phoneNumber("+1234567890")
                .build();
        user.setEmailConfirmed(true);

        doReturn(user).when(userDataService).getUserByEmail(email);
        doReturn(true).when(userDataService).isSamePassword(user.getPassword(), rawPassword);
        when(jwtTokenService.generateTokenForUser(user)).thenReturn(null);

        String token = userDataService.loginUser(email, rawPassword);

        assertNull(token);
    }
}
