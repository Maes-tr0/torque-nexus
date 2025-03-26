package ua.torque.nexus.feature.token.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.user.model.User;

import java.util.Optional;

@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByUser(User user);

    Optional<ConfirmationToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM ConfirmationToken ct WHERE ct.confirmedAt IS NOT NULL AND ct.user.emailConfirmed = true")
    int deleteByConfirmedTrue();
}