package ua.torque.nexus.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.user.model.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.emailConfirmed = false AND u.created < :cutoff")
    List<User> findByEmailConfirmedFalseAndCreatedDateBefore(@Param("cutoff") Date cutoff);
}