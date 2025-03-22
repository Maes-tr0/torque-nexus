package ua.torque.nexus.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.model.Role;

import java.util.Optional;

@Transactional(readOnly = true)
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String roleName);
}
