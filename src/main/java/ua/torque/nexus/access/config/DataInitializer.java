package ua.torque.nexus.access.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.model.permission.Permission;
import ua.torque.nexus.access.model.permission.PermissionType;
import ua.torque.nexus.access.model.role.Role;
import ua.torque.nexus.access.model.role.RoleType;
import ua.torque.nexus.access.repository.PermissionRepository;
import ua.torque.nexus.access.repository.RoleRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Starting initialization of default roles and permissions...");

        Map<PermissionType, Permission> availablePermissions = ensurePermissionsExist();

        synchronizeRolesAndPermissions(availablePermissions);

        log.info("Default roles and permissions initialization check complete.");
    }

    private Map<PermissionType, Permission> ensurePermissionsExist() {
        Map<PermissionType, Permission> existingPermissions = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(Permission::getType, Function.identity()));

        Set<PermissionType> missingPermissionTypes = Arrays.stream(PermissionType.values())
                .filter(type -> !existingPermissions.containsKey(type))
                .collect(Collectors.toSet());

        if (!missingPermissionTypes.isEmpty()) {
            log.info("Found {} missing permissions. Creating them...", missingPermissionTypes.size());
            var newPermissions = missingPermissionTypes.stream()
                    .map(type -> Permission.builder().type(type).build())
                    .toList();
            permissionRepository.saveAll(newPermissions);

            existingPermissions.putAll(permissionRepository.findAll().stream()
                    .collect(Collectors.toMap(Permission::getType, Function.identity())));
        }

        return existingPermissions;
    }

    private void synchronizeRolesAndPermissions(Map<PermissionType, Permission> availablePermissions) {
        Arrays.stream(RoleType.values()).forEach(roleType -> {
            Role role = roleRepository.findByType(roleType)
                    .orElseGet(() -> {
                        log.info("Role '{}' not found. Creating new role.", roleType);
                        return Role.builder().type(roleType).build();
                    });

            Set<PermissionType> requiredPermissionTypes = AccessControlDefaults.getPermissionsFor(roleType);

            Set<Permission> requiredPermissions = requiredPermissionTypes.stream()
                    .map(availablePermissions::get)
                    .collect(Collectors.toSet());

            if (!role.getPermissions().equals(requiredPermissions)) {
                log.info("Updating permissions for role '{}'.", roleType);
                role.setPermissions(requiredPermissions);
                roleRepository.save(role);
                log.info("Successfully updated role '{}' with {} permissions.", roleType, requiredPermissions.size());
            } else {
                log.debug("Role '{}' permissions are already up-to-date.", roleType);
            }
        });
    }
}