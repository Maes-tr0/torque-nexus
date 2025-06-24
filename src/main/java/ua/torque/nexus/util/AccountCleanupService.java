package ua.torque.nexus.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCleanupService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteUnconfirmedUsers() {
        Date cutoffDate = Date.from(LocalDateTime.now().minusDays(1)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        List<User> unconfirmedUsers =
                userRepository.findByEmailConfirmedFalseAndCreatedDateBefore(cutoffDate);
        if (!unconfirmedUsers.isEmpty()) {
            log.info("Deleting {} unconfirmed users", unconfirmedUsers.size());
            userRepository.deleteAll(unconfirmedUsers);
        } else {
            log.info("No unconfirmed users to delete");
        }
    }
}
