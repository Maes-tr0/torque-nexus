package ua.torque.nexus.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.model.permission.Permission;
import ua.torque.nexus.access.model.permission.PermissionType;

import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByTypeIn(Collection<PermissionType> types);
}
