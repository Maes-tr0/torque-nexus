package ua.torque.nexus.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.model.Permission;

import java.util.List;

@Transactional(readOnly = true)
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean findByName(String name);

    @Query("SELECT p FROM Permission p WHERE p.name IN ('CHANGE_PASSWORD', 'DELETE_ACCOUNT', 'UPDATE_PASSWORD')")
    List<Permission> customerPermission();
}