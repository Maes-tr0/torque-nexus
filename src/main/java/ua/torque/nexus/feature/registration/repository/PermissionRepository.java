package ua.torque.nexus.feature.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.feature.registration.model.Permission;

import java.util.Optional;

@Transactional(readOnly = true)
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}