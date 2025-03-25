package ua.torque.nexus.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("""
            SELECT u
            FROM ConfirmationToken ct
            JOIN ct.user u
            WHERE u.emailConfirmed = false
              AND ct.expiresAt < :now
            """)
    List<User> findAllByEmailConfirmedFalseAndConfirmationTokenExpiresAtBefore(LocalDateTime dateTime);

}