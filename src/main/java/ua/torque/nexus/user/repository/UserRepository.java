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

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<User> findByEmailWithAuthorities(String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.emailConfirmed = false AND u.created < :cutoff")
    int deleteUnconfirmedUsersBefore(@Param("cutoff") LocalDateTime cutoff);
}