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

import java.util.HashSet;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessControlService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @PostConstruct
    @Transactional
    public void initializeRolesAndPermissions() {
        for (PermissionType permType : PermissionType.values()) {
            if (!permissionRepository.existsByName(permType.name())) {
                permissionRepository.save(
                        Permission.builder()
                                .name(permType.name())
                                .description(permType.getDescription())
                                .build()
                );
                log.info("Created permission {}", permType.name());
            }
        }


        for (RoleType roleType : RoleType.values()) {
            Role role = roleRepository.findByName(roleType.name())
                    .orElseGet(() -> roleRepository.save(
                            Role.builder()
                                    .name(roleType.name())
                                    .build()
                    ));

            switch (roleType) {
                case RoleType.ADMIN ->
                        role.setPermissions(new HashSet<>(permissionRepository.findAll()));
                case RoleType.CUSTOMER ->
                        role.setPermissions(new HashSet<>(permissionRepository.customerPermission()));
                default ->
                        throw new UnsupportedRoleTypeException(
                                "Unsupported role type: " + roleType,
                                Map.of("unsupportedRoleType", roleType.name())
                        );
            }

            roleRepository.save(role);
            log.info("Initialized role '{}' with permissions {}", role.getName(), role.getPermissions());
        }
    }

    @Transactional
    public void assignRoleToUser(User user, RoleType roleType) {
        Role persistedRole = roleRepository.findByName(roleType.name())
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleType.name()));

        user.setRole(persistedRole);
        log.info("Assigned role '{}' to user {}", persistedRole.getName(), user.getEmail());
    }
}
