package ua.torque.nexus.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCleanupService {

    private final UserRepository userRepository;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteUnconfirmedUsers() {
        final String jobName = "DeleteUnconfirmedUsers";
        log.info("event=scheduled_job_started jobName={}", jobName);

        final LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        final int deletedCount = userRepository.deleteUnconfirmedUsersBefore(cutoff);

        if (deletedCount > 0) {
            log.info("event=scheduled_job_finished jobName={} status=success deletedCount={} cutoffTime={}",
                    jobName, deletedCount, dtf.format(cutoff));
        } else {
            log.info("event=scheduled_job_finished jobName={} status=success message=\"No unconfirmed users found to delete\" cutoffTime={}",
                    jobName, dtf.format(cutoff));
        }
    }
}