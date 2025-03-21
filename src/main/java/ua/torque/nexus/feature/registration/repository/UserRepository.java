package ua.torque.nexus.feature.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.torque.nexus.feature.registration.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
