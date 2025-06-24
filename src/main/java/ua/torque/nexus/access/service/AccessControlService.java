package ua.torque.nexus.access.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.config.PermissionConfig;
import ua.torque.nexus.access.model.permission.Permission;
import ua.torque.nexus.access.model.permission.PermissionType;
import ua.torque.nexus.access.model.role.Role;
import ua.torque.nexus.access.model.role.RoleType;
import ua.torque.nexus.access.repository.PermissionRepository;
import ua.torque.nexus.access.repository.RoleRepository;
import ua.torque.nexus.user.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionConfig permissionConfig;


    @PostConstruct
    @Transactional
    public void initializeRolesAndPermissions() {
        log.info("Starting initialization of roles and permissions...");

        ensurePermissionsExist();
        synchronizeRoles();

        log.info("Initialization finished.");
    }


    private void ensurePermissionsExist() {
        Set<PermissionType> existingPermissionTypes = permissionRepository.findAll().stream()
                .map(Permission::getType)
                .collect(Collectors.toSet());

        List<Permission> newPermissions = Arrays.stream(PermissionType.values())
                .filter(permType -> !existingPermissionTypes.contains(permType))
                .map(permType -> Permission.builder()
                        .type(permType)
                        .description(permType.getDescription())
                        .build())
                .toList();

        if (!newPermissions.isEmpty()) {
            permissionRepository.saveAll(newPermissions);
            newPermissions.forEach(p -> log.info("Created permission: {}", p.getType()));
        } else {
            log.info("All permission types already exist in the database.");
        }
    }


    private void synchronizeRoles() {
        Map<PermissionType, Permission> allPermissionsMap = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(Permission::getType, Function.identity()));

        Map<RoleType, Role> existingRolesMap = roleRepository.findAll().stream()
                .collect(Collectors.toMap(Role::getType, Function.identity()));

        List<Role> rolesToSave = Arrays.stream(RoleType.values())
                .map(roleType -> {
                    Role role = existingRolesMap.getOrDefault(roleType, Role.builder().type(roleType).build());
                    Set<PermissionType> requiredPermissionTypes = getPermissionTypesForRole(roleType);

                    Set<Permission> permissionsToSet = requiredPermissionTypes.stream()
                            .map(allPermissionsMap::get)
                            .collect(Collectors.toSet());

                    role.setPermissions(permissionsToSet);
                    log.info("Initializing role '{}' with permissions: {}", role.getType(), requiredPermissionTypes);
                    return role;
                })
                .toList();

        roleRepository.saveAll(rolesToSave);
        log.info("Successfully initialized {} roles.", rolesToSave.size());
    }


    private Set<PermissionType> getPermissionTypesForRole(RoleType roleType) {
        return switch (roleType) {
            case ADMIN -> permissionConfig.getDefaultAdminPermissionTypes();
            case CUSTOMER -> permissionConfig.getDefaultUserPermissionTypes();
        };
    }


    @Transactional
    public void assignRoleToUser(User user, RoleType roleType) {
        Role persistedRole = roleRepository.findByType(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleType.name()));

        user.setRole(persistedRole);
        log.info("Assigned role '{}' to user {}", persistedRole.getType().name(), user.getEmail());
    }
}
