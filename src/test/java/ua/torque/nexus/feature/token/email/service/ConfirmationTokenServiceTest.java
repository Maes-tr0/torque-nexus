package ua.torque.nexus.feature.token.email.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.access.exception.UserSaveException;
import ua.torque.nexus.feature.token.email.exception.EmailAlreadyConfirmedException;
import ua.torque.nexus.feature.token.email.exception.TokenExpiredException;
import ua.torque.nexus.feature.token.email.exception.TokenNotFoundException;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.repository.ConfirmationTokenRepository;
import ua.torque.nexus.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    private ConfirmationTokenService confirmationTokenService;
    private final long expiryHours = 2L;

    @BeforeEach
    void setUp() {
        confirmationTokenService = new ConfirmationTokenService(confirmationTokenRepository, expiryHours);
    }

    @Test
    void generateTokenForUser_returnsTokenWithCorrectExpiry() {
        User user = new User();
        user.setEmail("test@example.com");
        when(confirmationTokenRepository.save(any(ConfirmationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ConfirmationToken generated = confirmationTokenService.generateTokenForUser(user);
        long hoursDiff = ChronoUnit.HOURS.between(generated.getCreatedAt(), generated.getExpiresAt());
        assertThat(generated.getToken()).isNotBlank();
        assertThat(hoursDiff).isEqualTo(expiryHours);
        assertThat(generated.getUser()).isEqualTo(user);
        verify(confirmationTokenRepository).save(any(ConfirmationToken.class));
    }

    @Test
    void confirmToken_whenTokenNotFound_throwsTokenNotFoundException() {
        String tokenValue = "non-existent";
        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> confirmationTokenService.confirmToken(tokenValue));
        assertThat(thrown).isInstanceOf(TokenNotFoundException.class)
                .hasMessageContaining("Token not found: " + tokenValue);
    }

    @Test
    void confirmToken_whenExpired_throwsTokenExpiredException() {
        String tokenValue = "expired";
        User user = new User();
        user.setEmail("expired@example.com");
        user.setEmailConfirmed(false);
        ConfirmationToken token = ConfirmationToken.builder()
                .token(tokenValue)
                .createdAt(LocalDateTime.now().minusHours(3))
                .expiresAt(LocalDateTime.now().minusHours(1))
                .user(user)
                .build();
        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));
        Throwable thrown = catchThrowable(() -> confirmationTokenService.confirmToken(tokenValue));
        assertThat(thrown).isInstanceOf(TokenExpiredException.class)
                .hasMessageContaining("Token expired: " + tokenValue);
    }

    @Test
    void confirmToken_whenEmailAlreadyConfirmed_throwsEmailAlreadyConfirmedException() {
        String tokenValue = "confirmed";
        User user = new User();
        user.setEmail("confirmed@example.com");
        user.setEmailConfirmed(true);
        ConfirmationToken token = ConfirmationToken.builder()
                .token(tokenValue)
                .createdAt(LocalDateTime.now().minusHours(1))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .user(user)
                .build();
        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));
        Throwable thrown = catchThrowable(() -> confirmationTokenService.confirmToken(tokenValue));
        assertThat(thrown).isInstanceOf(EmailAlreadyConfirmedException.class)
                .hasMessageContaining("Email is already confirmed for user: " + user.getEmail());
    }

    @Test
    void confirmToken_whenSaveFails_throwsUserSaveException() {
        String tokenValue = "fail";
        User user = new User();
        user.setEmail("fail@example.com");
        user.setEmailConfirmed(false);
        ConfirmationToken token = ConfirmationToken.builder()
                .token(tokenValue)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .user(user)
                .build();
        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));
        when(confirmationTokenRepository.save(any(ConfirmationToken.class)))
                .thenThrow(new RuntimeException("DB error"));
        Throwable thrown = catchThrowable(() -> confirmationTokenService.confirmToken(tokenValue));
        assertThat(thrown).isInstanceOf(UserSaveException.class)
                .hasMessageContaining("Failed to confirm token: " + tokenValue);
        UserSaveException ex = (UserSaveException) thrown;
        assertThat(ex.getDetails()).containsEntry("cause", "RuntimeException")
                .containsEntry("message", "DB error");
    }

    @Test
    void confirmToken_successfulConfirmation() {
        String tokenValue = "valid";
        User user = new User();
        user.setEmail("valid@example.com");
        user.setEmailConfirmed(false);
        ConfirmationToken token = ConfirmationToken.builder()
                .token(tokenValue)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .user(user)
                .build();
        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));
        when(confirmationTokenRepository.save(any(ConfirmationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ConfirmationToken confirmed = confirmationTokenService.confirmToken(tokenValue);
        assertThat(confirmed.getConfirmedAt()).isNotNull();
        assertThat(user.isEmailConfirmed()).isTrue();
        verify(confirmationTokenRepository).findByToken(tokenValue);
        verify(confirmationTokenRepository).save(any(ConfirmationToken.class));
    }
}
