package ua.torque.nexus.feature.token.email.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.token.email.model.dto.ConfirmationResponse;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.repository.UserRepository;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.repository.ConfirmationTokenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ConfirmationTokenDataService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;
    private final long EXPIRATION_TIME;


    public ConfirmationTokenDataService(ConfirmationTokenRepository confirmationTokenRepository,
                                        UserRepository userRepository,
                                        @Value("${registration.token.expiry.hour}") long expirationTime) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userRepository = userRepository;
        this.EXPIRATION_TIME = expirationTime;
    }

    public void saveConfirmationToken(User user) {
        ConfirmationToken confirmationToken = generateConfirmationToken();

        confirmationToken.setUser(user);

        confirmationTokenRepository.save(confirmationToken);
    }

    private void updateConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationToken.setConfirmedAt(LocalDateTime.now());

        confirmationTokenRepository.save(confirmationToken);
    }

    public ConfirmationToken getConfirmationToken(User user) {
        return confirmationTokenRepository.findByUser(user);
    }

    private ConfirmationToken generateConfirmationToken() {
        String token = UUID.randomUUID().toString();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusHours(EXPIRATION_TIME);

        return ConfirmationToken.builder()
                .token(token)
                .createdAt(now)
                .expiresAt(expiryDate)
                .build();
    }

    @Scheduled(cron = "@midnight")
    void deleteExpiredTokensAndUsers() {
        LocalDateTime now = LocalDateTime.now();
        List<ConfirmationToken> expired = confirmationTokenRepository.findAllByExpiresAtBefore(now);

        expired.forEach(token -> {
            User user = token.getUser();
            confirmationTokenRepository.delete(token);
            userRepository.delete(user);
            log.info("Deleted expired token {} and user {}", token.getToken(), user.getEmail());
        });
    }

    public ConfirmationResponse confirmToken(String token) {
        ConfirmationToken confirmToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (LocalDateTime.now().isBefore(confirmToken.getExpiresAt())) {
            updateConfirmationToken(confirmToken);
            return new ConfirmationResponse(confirmToken.getToken(), confirmToken.getConfirmedAt());
        }

        throw new RuntimeException("Expired token");
    }

}