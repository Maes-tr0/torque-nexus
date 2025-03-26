package ua.torque.nexus.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.torque.nexus.feature.token.email.repository.ConfirmationTokenRepository;
import ua.torque.nexus.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CleanupServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @InjectMocks
    private CleanupService cleanupService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("deleteUnconfirmedUsersWithExpiredTokens: якщо репозиторій повертає 0, логується 0")
    void deleteUnconfirmedUsersWithExpiredTokens_whenNoUsersDeleted_thenLogZero() {
        when(userRepository.deleteByEmailNotConfirmedAndTokenExpiredBefore(any(LocalDateTime.class)))
                .thenReturn(0);

        cleanupService.deleteUnconfirmedUsersWithExpiredTokens();

        verify(userRepository, times(1))
                .deleteByEmailNotConfirmedAndTokenExpiredBefore(any(LocalDateTime.class));
        // Перевірити лог – зазвичай роблять або LogCaptor, або просто переконуються, що не було винятків.
    }

    @Test
    @DisplayName("deleteUnconfirmedUsersWithExpiredTokens: якщо видалено декілька записів, повертається > 0")
    void deleteUnconfirmedUsersWithExpiredTokens_whenSomeUsersDeleted() {
        when(userRepository.deleteByEmailNotConfirmedAndTokenExpiredBefore(any(LocalDateTime.class)))
                .thenReturn(5);

        cleanupService.deleteUnconfirmedUsersWithExpiredTokens();

        verify(userRepository, times(1))
                .deleteByEmailNotConfirmedAndTokenExpiredBefore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("deleteUnconfirmedUsersWithExpiredTokens: якщо репозиторій кидає виняток, він пролітає далі")
    void deleteUnconfirmedUsersWithExpiredTokens_whenRepositoryThrows_thenPropagateException() {
        when(userRepository.deleteByEmailNotConfirmedAndTokenExpiredBefore(any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () ->
                cleanupService.deleteUnconfirmedUsersWithExpiredTokens()
        );
        verify(userRepository).deleteByEmailNotConfirmedAndTokenExpiredBefore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("deleteUnconfirmedUsersWithExpiredTokens: перевірка, що переданий час – зараз мінус 1 хвилина")
    void deleteUnconfirmedUsersWithExpiredTokens_shouldUseTimeOneMinuteAgo() {
        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        when(userRepository.deleteByEmailNotConfirmedAndTokenExpiredBefore(any(LocalDateTime.class)))
                .thenReturn(1);

        cleanupService.deleteUnconfirmedUsersWithExpiredTokens();

        verify(userRepository).deleteByEmailNotConfirmedAndTokenExpiredBefore(captor.capture());
        LocalDateTime cutoff = captor.getValue();
        LocalDateTime nowMinusOneMinute = LocalDateTime.now().minusMinutes(1);

        long diffInSeconds = Math.abs(java.time.Duration.between(nowMinusOneMinute, cutoff).getSeconds());
        assertTrue(diffInSeconds < 5,
                () -> "Очікувалося, що cutoff ~ now() - 1 хв, але різниця = " + diffInSeconds + " секунд");
    }

    @Test
    @DisplayName("deleteConfirmedTokens: якщо видалено 0 підтверджених токенів, просто логуємо 0")
    void deleteConfirmedTokens_whenNoTokensDeleted_thenLogZero() {
        when(confirmationTokenRepository.deleteByConfirmedTrue()).thenReturn(0);

        cleanupService.deleteConfirmedTokens();

        verify(confirmationTokenRepository).deleteByConfirmedTrue();
    }

    @Test
    @DisplayName("deleteConfirmedTokens: якщо видалено декілька токенів")
    void deleteConfirmedTokens_whenSomeTokensDeleted() {
        when(confirmationTokenRepository.deleteByConfirmedTrue()).thenReturn(10);

        cleanupService.deleteConfirmedTokens();

        verify(confirmationTokenRepository).deleteByConfirmedTrue();
    }

    @Test
    @DisplayName("deleteConfirmedTokens: якщо виникає виняток, він пролітає далі")
    void deleteConfirmedTokens_whenRepositoryThrows_thenPropagateException() {
        doThrow(new RuntimeException("DB error"))
                .when(confirmationTokenRepository).deleteByConfirmedTrue();

        assertThrows(RuntimeException.class, () ->
                cleanupService.deleteConfirmedTokens()
        );
        verify(confirmationTokenRepository).deleteByConfirmedTrue();
    }

}
