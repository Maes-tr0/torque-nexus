package ua.torque.nexus.feature.token.email.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.feature.token.email.exception.TokenExpiredException;
import ua.torque.nexus.feature.token.email.exception.TokenNotFoundException;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.repository.ConfirmationTokenRepository;
import ua.torque.nexus.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final long expiryHoursTime;

    public ConfirmationTokenService(
            ConfirmationTokenRepository confirmationTokenRepository,
            @Value("${registration.token.expiry.hour}") long expiryHoursTime) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.expiryHoursTime = expiryHoursTime;
    }

    @Transactional
    public void generateTokenForUser(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        ConfirmationToken token = ConfirmationToken.builder()
                .token(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(expiryHoursTime))
                .user(user)
                .build();

        ConfirmationToken saved = confirmationTokenRepository.save(token);
        log.info("Generated confirmation token={} for user={}", saved.getToken(), user.getEmail());
    }

    @Transactional
    public ConfirmationToken confirmToken(String tokenValue) {
        ConfirmationToken token = confirmationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenNotFoundException(tokenValue));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(token.getToken());
        }

        token.setConfirmedAt(LocalDateTime.now());
        token.getUser().setEmailConfirmed(true);

        return confirmationTokenRepository.save(token);
    }
}
