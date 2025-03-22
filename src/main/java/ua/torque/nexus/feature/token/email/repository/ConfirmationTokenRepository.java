package ua.torque.nexus.feature.token.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    List<ConfirmationToken> findAllByExpiresAtBefore(LocalDateTime dateTime);

    ConfirmationToken findByUser(User user);

    Optional<ConfirmationToken> findByToken(String token);
}