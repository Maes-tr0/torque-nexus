package ua.torque.nexus.feature.token.email.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.exception.UserSaveException;
import ua.torque.nexus.feature.token.email.exception.EmailAlreadyConfirmedException;
import ua.torque.nexus.feature.token.email.exception.TokenExpiredException;
import ua.torque.nexus.feature.token.email.exception.TokenNotFoundException;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.repository.ConfirmationTokenRepository;
import ua.torque.nexus.user.model.User;

import java.time.LocalDateTime;
import java.util.Map;
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
    public ConfirmationToken generateTokenForUser(User user) {
        ConfirmationToken token = ConfirmationToken.builder()
                .token(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(expiryHoursTime))
                .user(user)
                .build();

        ConfirmationToken saved = confirmationTokenRepository.save(token);
        log.info("Generated confirmation token={} for user={}", saved.getToken(), user.getEmail());

        return saved;
    }

    @Transactional
    public ConfirmationToken confirmToken(String tokenValue) {
        ConfirmationToken token = confirmationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenNotFoundException("Token not found: " + tokenValue));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired: " + tokenValue);
        }

        User user = token.getUser();
        if (user.isEmailConfirmed()) {
            throw new EmailAlreadyConfirmedException("Email is already confirmed for user: " + user.getEmail());
        }

        try {
            token.setConfirmedAt(LocalDateTime.now());
            user.setEmailConfirmed(true);
            return confirmationTokenRepository.save(token);
        } catch (Exception e) {
            log.error("Error confirming token {} for user {}", tokenValue, user.getEmail(), e);
            throw new UserSaveException(
                    "Failed to confirm token: " + tokenValue,
                    Map.of("cause", e.getClass().getSimpleName(), "message", e.getMessage())
            );
        }
    }

}
