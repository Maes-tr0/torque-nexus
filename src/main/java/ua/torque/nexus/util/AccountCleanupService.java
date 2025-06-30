package ua.torque.nexus.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCleanupService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteUnconfirmedUsers() {
        log.info("Starting scheduled job: DeleteUnconfirmedUsers...");

        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);

        int deletedCount = userRepository.deleteUnconfirmedUsersBefore(cutoff);

        if (deletedCount > 0) {
            log.info("Successfully deleted {} unconfirmed user(s) created before {}.", deletedCount, cutoff);
        } else {
            log.info("No unconfirmed users found to delete.");
        }
        log.info("Finished scheduled job: DeleteUnconfirmedUsers.");
    }
}