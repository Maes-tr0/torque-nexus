package ua.torque.nexus.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.model.permission.Permission;

@Transactional(readOnly = true)
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
