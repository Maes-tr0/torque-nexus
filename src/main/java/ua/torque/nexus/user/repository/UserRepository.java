package ua.torque.nexus.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.emailConfirmed = false AND u.id IN (" +
            "SELECT ct.user.id FROM ConfirmationToken ct WHERE ct.expiresAt < :cutoff)")
    int deleteByEmailNotConfirmedAndTokenExpiredBefore(@Param("cutoff") LocalDateTime cutoff);

}