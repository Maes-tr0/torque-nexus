package ua.torque.nexus.access.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.exception.UnsupportedRoleTypeException;
import ua.torque.nexus.access.model.Permission;
import ua.torque.nexus.access.model.PermissionType;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;
import ua.torque.nexus.access.repository.PermissionRepository;
import ua.torque.nexus.access.repository.RoleRepository;
import ua.torque.nexus.user.model.User;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessControlService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final Set<Permission> defaultAdminPermissions;
    private final Set<Permission> defaultUserPermissions;

    @PostConstruct
    @Transactional
    public void initializeRolesAndPermissions() {
        for (PermissionType permType : PermissionType.values()) {
            if (!permissionRepository.existsByType(permType)) {
                permissionRepository.save(
                        Permission.builder()
                                .type(permType)
                                .description(permType.getDescription())
                                .build()
                );
                log.info("Created permission {}", permType);
            }
        }

        for (RoleType roleType : RoleType.values()) {
            Role role = roleRepository.findByType(roleType)
                    .orElseGet(() -> roleRepository.save(
                            Role.builder()
                                    .type(roleType)
                                    .build()
                    ));

            switch (roleType) {
                case ADMIN -> role.setPermissions(defaultAdminPermissions);
                case CUSTOMER -> role.setPermissions(defaultUserPermissions);
                default -> throw new UnsupportedRoleTypeException(
                        "Unsupported role type: " + roleType,
                        Map.of("unsupportedRoleType", roleType.name())
                );
            }

            roleRepository.save(role);
            log.info("Initialized role '{}' with permissions {}", role.getType(), role.getPermissions());
        }
    }

    @Transactional
    public void assignRoleToUser(User user, RoleType roleType) {
        Role persistedRole = roleRepository.findByType(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleType.name()));

        user.setRole(persistedRole);
        log.info("Assigned role '{}' to user {}", persistedRole.getType().name(), user.getEmail());
    }
}
