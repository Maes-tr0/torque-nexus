package ua.torque.nexus.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.feature.token.email.repository.ConfirmationTokenRepository;
import ua.torque.nexus.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupService {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;


    @Scheduled(cron = " 0 * * * * ?")
    @Transactional
    public void deleteUnconfirmedUsersWithExpiredTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(1);
        int usersDeleted = userRepository.deleteByEmailNotConfirmedAndTokenExpiredBefore(cutoff);
        log.info("Deleted {} unconfirmed users with expired tokens.", usersDeleted);
    }


    @Scheduled(cron = "0 0 0 */2 * ?")
    @Transactional
    public void deleteConfirmedTokens() {
        int tokensDeleted = confirmationTokenRepository.deleteByConfirmedTrue();
        log.info("Deleted {} confirmed tokens (cleanup every 2 days)", tokensDeleted);
    }
}
